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

    @RabbitListener(queues = SeckillMqConfig.QUEUE, concurrency = "10")
    public void onSeckillSuccess(CouponSeckillEvent event) {
        log.info("收到秒杀事件: couponId={}, userId={}", event.getCouponId(), event.getUserId());

        // 事务化落库
        if (orderPersister.persist(event.getCouponId(), event.getUserId())) {
            log.info("秒杀落库成功: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        } else {
            log.warn("秒杀落库跳过（重复领取）: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        }
    }
}
