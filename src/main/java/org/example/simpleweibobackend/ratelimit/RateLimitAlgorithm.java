package org.example.simpleweibobackend.ratelimit;

/** 可插拔的限流算法 */
public enum RateLimitAlgorithm {

    /** 固定窗口: 窗口内计数超过阈值即拒绝, 实现最简单, 但窗口边界处可能放进接近 2 倍的流量 */
    FIXED_WINDOW,

    /** 滑动窗口: 统计当前时刻往前一个窗口内的请求数, 解决固定窗口的边界突刺问题 */
    SLIDING_WINDOW,

    /** 令牌桶: 桶容量为 limit, 按 limit/window 的速率补充令牌, 平稳限速的同时允许突发流量 */
    TOKEN_BUCKET
}
