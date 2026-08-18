package org.example.simpleweibobackend.coupon.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.dto.CouponSeckillEvent;
import org.example.simpleweibobackend.coupon.dto.SeckillCorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.List;

/**
 * 秒杀 MQ 可靠性回调：broker nack（消息未被正确持久化）时，对 Redis 预扣减做原子回滚。
 * 回滚脚本幂等，同步发送失败补偿与异步回调补偿并存也不会多回滚。
 * 注：消息不可路由（ReturnsCallback）防护已移除——开发阶段保证 queue/binding 存在、消息可路由。
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SeckillMqConfirmConfig {

    private final RabbitTemplate rabbitTemplate;
    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> seckillRollbackScript;

    @PostConstruct
    public void init() {
        rabbitTemplate.setConfirmCallback(this::onConfirm);
    }

    // broker 确认失败（nack）：消息未被 broker 正确持久化，回滚 Redis 预扣减
    private void onConfirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            return;
        }
        if (!(correlationData instanceof SeckillCorrelationData cd)) {
            log.error("[秒杀MQ告警] broker nack 但关联数据缺失, correlationData={}, cause={}", correlationData, cause);
            return;
        }
        log.error("[秒杀MQ告警] broker nack, cause={}", cause);
        rollbackRedis(cd.getEvent());
    }

    private void rollbackRedis(CouponSeckillEvent event) {
        try {
            String stockKey = "coupon:stock:" + event.getCouponId();
            String userKey = "coupon:user:" + event.getCouponId();
            Long rolledBack = redisTemplate.execute(seckillRollbackScript,
                    List.of(stockKey, userKey), event.getUserId().toString());
            log.error("[秒杀MQ告警] Redis回滚完成: event={}, rolledBack={}", event, rolledBack);
        } catch (Exception e) {
            log.error("[秒杀MQ告警] Redis回滚失败，需人工核对 coupon:stock:{}/coupon:user:{} 与 user_coupon 表",
                    event.getCouponId(), event.getCouponId(), e);
        }
    }
}
