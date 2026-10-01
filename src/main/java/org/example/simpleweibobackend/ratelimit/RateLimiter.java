package org.example.simpleweibobackend.ratelimit;

import java.time.Duration;

/** 限流算法策略接口, 新算法只需实现本接口并注册为 Spring Bean, 即可在注解中选用 */
public interface RateLimiter {

    /** 本实现支持的算法类型 */
    RateLimitAlgorithm algorithm();

    /**
     * 尝试获取一次配额
     *
     * @param key    限流 key, 已包含 userId 维度
     * @param limit  窗口内最大请求次数, 令牌桶算法下同时作为桶容量
     * @param window 窗口时长
     * @return true 放行, false 拒绝
     */
    boolean tryAcquire(String key, long limit, Duration window);
}
