package org.example.simpleweibobackend.coupon.consumer;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.coupon.entity.UserCoupon;
import org.example.simpleweibobackend.coupon.mapper.CouponMapper;
import org.example.simpleweibobackend.coupon.mapper.UserCouponMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 秒杀落库事务组件：insert 领取记录 + 扣减库存原子提交，供主/DLQ 消费者共享。
 * decrement 失败时 insert 一并回滚，消息重试可重新执行——
 * 避免"insert 成功但 stock 未扣"的永久漂移（重试时 insertIgnore 不会再被残留记录挡住）。
 */
@Component
@RequiredArgsConstructor
public class SeckillOrderPersister {

    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;

    /**
     * @return true=落库成功；false=重复领取（此前已落库，insertIgnore 未插入）
     */
    @Transactional
    public boolean persist(Long couponId, Long userId) {
        UserCoupon userCoupon = new UserCoupon();
        userCoupon.setUserId(userId);
        userCoupon.setCouponId(couponId);
        userCoupon.setStatus("UNUSED");
        int inserted = userCouponMapper.insertIgnore(userCoupon);
        if (inserted == 0) {
            return false;
        }
        couponMapper.decrementStock(couponId);
        return true;
    }
}
