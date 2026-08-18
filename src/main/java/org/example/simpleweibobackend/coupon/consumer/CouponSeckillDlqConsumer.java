package org.example.simpleweibobackend.coupon.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.config.SeckillMqConfig;
import org.example.simpleweibobackend.coupon.dto.CouponSeckillEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 秒杀死信消费者：主队列重试耗尽后做最后一次落库补偿。
 * 全程捕获异常不外抛——DLQ 无再次死信路由，抛异常将导致消息被直接丢弃，仅剩日志。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CouponSeckillDlqConsumer {

    private final SeckillOrderPersister orderPersister;

    @RabbitListener(queues = SeckillMqConfig.DLQ)
    public void onSeckillDead(CouponSeckillEvent event) {
        log.error("[秒杀DLQ告警] 落库重试耗尽进入死信，尝试补偿: couponId={}, userId={}",
                event.getCouponId(), event.getUserId());
        try {
            // 事务化落库：失败时 persist 内部事务已回滚，此处仅告警
            if (orderPersister.persist(event.getCouponId(), event.getUserId())) {
                log.warn("[秒杀DLQ] 补偿落库成功: couponId={}, userId={}",
                        event.getCouponId(), event.getUserId());
            } else {
                log.warn("[秒杀DLQ] 补偿跳过（此前已落库，属重复消息）: couponId={}, userId={}",
                        event.getCouponId(), event.getUserId());
            }
        } catch (Exception e) {
            log.error("[秒杀DLQ告警] 补偿落库失败，需人工核对 Redis 扣减与 user_coupon/coupon 表: couponId={}, userId={}",
                    event.getCouponId(), event.getUserId(), e);
        }
    }
}
