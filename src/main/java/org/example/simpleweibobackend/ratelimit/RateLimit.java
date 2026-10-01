package org.example.simpleweibobackend.ratelimit;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * 基于 userId 的接口限流注解
 *
 * <p>限流维度为 userId + 业务 key, 触发限流抛出 429 业务异常。
 * 要求接口处于登录态(userId 取自 UserContext), 因此不要标注在免登录接口上。
 *
 * <p>使用示例:
 * <pre>{@code
 * // 每个用户 60 秒内最多发 5 条微博, 使用滑动窗口算法
 * @RateLimit(key = "post:create", limit = 5, window = 60,
 *            timeUnit = TimeUnit.SECONDS, algorithm = RateLimitAlgorithm.SLIDING_WINDOW)
 * public Result<Void> create(CreatePostRequest request) { ... }
 * }</pre>
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 业务标识, 用于拼接 Redis key, 必填 */
    String key();

    /** 限流阈值: 窗口内最大请求次数, 令牌桶算法下同时作为桶容量 */
    long limit() default 10;

    /** 窗口时长, 令牌桶算法下按 limit/window 的速率补充令牌 */
    long window() default 60;

    /** 窗口时长单位 */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /** 限流算法, 默认固定窗口 */
    RateLimitAlgorithm algorithm() default RateLimitAlgorithm.FIXED_WINDOW;
}
