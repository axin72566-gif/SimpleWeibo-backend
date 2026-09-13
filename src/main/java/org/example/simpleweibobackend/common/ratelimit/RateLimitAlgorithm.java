package org.example.simpleweibobackend.common.ratelimit;

import org.example.simpleweibobackend.common.ratelimit.limiter.RateLimiter;

/**
 * 限流算法类型,对应 {@link RateLimiter} 的四种实现
 */
public enum RateLimitAlgorithm {

    /**
     * 固定窗口:按时间片计数,实现最简单,但窗口边界处可能放过 2 倍流量
     */
    FIXED_WINDOW,

    /**
     * 滑动窗口:精确统计"最近 N 毫秒"的请求数,无边界突刺,内存与请求量成正比
     */
    SLIDING_WINDOW,

    /**
     * 令牌桶:以恒定速率补充令牌,允许突发流量(最多消耗桶内积攒的令牌),适合秒杀入口
     */
    TOKEN_BUCKET,

    /**
     * 漏桶:以恒定速率放行,强制整流,适合保护处理能力有限的下游
     */
    LEAKY_BUCKET
}
