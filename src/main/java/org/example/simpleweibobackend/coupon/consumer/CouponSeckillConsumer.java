package org.example.simpleweibobackend.coupon.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.config.SeckillMqConfig;
import org.example.simpleweibobackend.coupon.dto.CouponSeckillEvent;
import org.example.simpleweibobackend.coupon.entity.UserCoupon;
import org.example.simpleweibobackend.coupon.mapper.CouponMapper;
import org.example.simpleweibobackend.coupon.mapper.UserCouponMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponSeckillConsumer {

    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;

    @RabbitListener(queues = SeckillMqConfig.QUEUE)
    public void onSeckillSuccess(CouponSeckillEvent event) {
        log.info("收到秒杀事件: couponId={}, userId={}", event.getCouponId(), event.getUserId());

        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(event.getUserId());
        userCoupon.setCouponId(event.getCouponId());
        userCoupon.setStatus("UNUSED");
        int inserted = userCouponMapper.insertIgnore(userCoupon);

        if (inserted > 0) {
            couponMapper.decrementStock(event.getCouponId());
            log.info("秒杀落库成功: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        } else {
            log.warn("秒杀落库跳过（重复领取）: couponId={}, userId={}", event.getCouponId(), event.getUserId());
        }
    }
}
