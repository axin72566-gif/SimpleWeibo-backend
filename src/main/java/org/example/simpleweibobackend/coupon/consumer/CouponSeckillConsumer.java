package org.example.simpleweibobackend.coupon.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.config.SeckillMqConfig;
import org.example.simpleweibobackend.coupon.dto.CouponSeckillEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponSeckillConsumer {

    private final SeckillOrderPersister orderPersister;

    // 并发消费降低积压：单线程约88 msg/s，10线程约880 msg/s
    @RabbitListener(queues = SeckillMqConfig.QUEUE, concurrency = "10")
    public void onSeckillSuccess(CouponSeckillEvent event) {
        log.info("收到秒杀事件: couponId={}, userId={}", event.getCouponId(), event.getUserId());

        // 事务化落库：异常时事务回滚并上抛，触发 MQ 重试，避免 insert 成功但 stock 未扣的漂移
        if (orderPersister.persist(event.getCouponId(), event.getUserId())) {
            log.info("秒杀落库成功: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        } else {
            log.warn("秒杀落库跳过（重复领取）: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        }
    }
}
