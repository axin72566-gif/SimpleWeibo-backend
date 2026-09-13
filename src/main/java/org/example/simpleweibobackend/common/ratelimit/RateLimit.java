package org.example.simpleweibobackend.common.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流注解,标注在 Controller/Service 方法上,由 {@link RateLimitAspect} 拦截生效
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /**
     * 阈值:窗口内最多放行多少次请求,默认 100 次
     */
    int limit() default 100;

    /**
     * 窗口时长(秒),默认 1 秒。配合 limit 即"每秒 100 次"
     */
    int window() default 1;

    /**
     * 限流维度:决定 key 拼接什么标识,即"限谁"
     */
    RateLimitDimension dimension() default RateLimitDimension.IP;

    /**
     * 限流算法,默认滑动窗口
     */
    RateLimitAlgorithm algorithm() default RateLimitAlgorithm.SLIDING_WINDOW;

    /**
     * 触发限流时返回给调用方的提示信息
     */
    String message() default "请求过于频繁,请稍后再试";
}
