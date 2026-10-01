package org.example.simpleweibobackend.ratelimit;

import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** 限流算法注册中心: 收集容器中所有 {@link RateLimiter} 实现并按算法类型分发 */
@Component
public class RateLimiterRegistry {

    private final Map<RateLimitAlgorithm, RateLimiter> limiters = new EnumMap<>(RateLimitAlgorithm.class);

    public RateLimiterRegistry(List<RateLimiter> limiterBeans) {
        limiterBeans.forEach(limiter -> limiters.put(limiter.algorithm(), limiter));
    }

    public RateLimiter get(RateLimitAlgorithm algorithm) {
        RateLimiter limiter = limiters.get(algorithm);
        if (limiter == null) {
            throw new IllegalStateException("未找到限流算法实现: " + algorithm);
        }
        return limiter;
    }
}
