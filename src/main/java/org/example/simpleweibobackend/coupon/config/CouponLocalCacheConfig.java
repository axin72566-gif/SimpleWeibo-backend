package org.example.simpleweibobackend.coupon.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.example.simpleweibobackend.coupon.entity.Coupon;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouponLocalCacheConfig {

    // 秒杀校验用本地缓存：coupon信息低频变更，不设TTL，一致性由变更点主动维护——
    // publish时put新实体，offline/delete时invalidate；startTime/endTime创建后不可变，
    // 缓存实体的stockRemaining可能滞后，但库存真值在Redis，不参与秒杀决策
    @Bean
    public Cache<Long, Coupon> couponCache() {
        return Caffeine.newBuilder()
                .maximumSize(1000)
                .build();
    }
}
