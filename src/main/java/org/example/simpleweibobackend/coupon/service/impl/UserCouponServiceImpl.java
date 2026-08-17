package org.example.simpleweibobackend.coupon.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.coupon.constant.CouponStatus;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.coupon.config.SeckillMqConfig;
import org.example.simpleweibobackend.coupon.dto.CouponSeckillEvent;
import org.example.simpleweibobackend.coupon.entity.Coupon;
import org.example.simpleweibobackend.coupon.mapper.CouponMapper;
import org.example.simpleweibobackend.coupon.service.UserCouponService;
import org.example.simpleweibobackend.coupon.vo.UserCouponVO;
import org.example.simpleweibobackend.exception.BizException;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {

    private final CouponMapper couponMapper;
    private final Cache<Long, Coupon> couponCache;
    private final StringRedisTemplate redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final DefaultRedisScript<Long> seckillScript;

    @Override
    public UserCouponVO seckill(Long couponId) {
        Long userId = UserContext.getUserId();

        // 1. 校验优惠券状态与时间窗口（本地缓存挡住热点查询，未命中才回源DB）
        Coupon coupon = couponCache.get(couponId, couponMapper::selectById);
        if (coupon == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "优惠券不存在");
        }
        if (coupon.getStatus() != CouponStatus.PUBLISHED) {
            throw new BizException(ErrorCode.BAD_REQUEST, "优惠券未上架");
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getStartTime())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "活动尚未开始");
        }
        if (now.isAfter(coupon.getEndTime())) {
            throw new BizException(ErrorCode.BAD_REQUEST, "活动已结束");
        }

        // 2. Redis Lua 原子扣减
        String stockKey = "coupon:stock:" + couponId;
        String userKey = "coupon:user:" + couponId;
        Long result = redisTemplate.execute(seckillScript, List.of(stockKey, userKey), userId.toString());

        if (result == null || result == -1L) {
            throw new BizException(ErrorCode.NOT_FOUND, "优惠券库存未初始化");
        }
        if (result == 1L) {
            throw new BizException(ErrorCode.BAD_REQUEST, "库存不足，手慢了");
        }
        if (result == 2L) {
            throw new BizException(ErrorCode.CONFLICT, "您已领取过该优惠券");
        }

        // 3. 发送 MQ 异步落库
        try {
            rabbitTemplate.convertAndSend(SeckillMqConfig.EXCHANGE, SeckillMqConfig.ROUTING_KEY,
                    new CouponSeckillEvent(couponId, userId));
        } catch (Exception e) {
            log.error("秒杀MQ发送失败，回滚Redis: couponId={}, userId={}", couponId, userId, e);
            redisTemplate.opsForValue().increment(stockKey);
            redisTemplate.opsForSet().remove(userKey, userId.toString());
            throw new BizException(ErrorCode.INTERNAL_ERROR, "系统繁忙，请重试");
        }

        return UserCouponVO.of(couponId, userId, coupon.getName(), coupon.getDiscountRate());
    }
}
