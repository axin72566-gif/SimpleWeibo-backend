package org.example.simpleweibobackend.common.ratelimit.limiter;

import org.example.simpleweibobackend.common.ratelimit.RateLimitAlgorithm;

/**
 * 限流器统一接口,所有限流算法都实现此方法。
 * 实现类需声明自己支持的算法,由 {@link org.example.simpleweibobackend.common.ratelimit.RateLimiterRegistry} 自动收集
 */
public interface RateLimiter {

    /**
     * 声明本实现支持的算法类型,注册表据此建立算法到实现的映射
     */
    RateLimitAlgorithm algorithm();

    /**
     * 判断一次请求是否放行
     *
     * @param key          限流对象标识,决定"限谁",同一 key 共享配额,不同 key 互不影响
     *                    例如:rate_limit:RegisterController#register:192.168.1.1
     * @param limit        阈值,窗口内最多放行多少次请求
     * @param windowMillis 窗口时长(毫秒),即"最近多长时间"的统计范围
     * @return true 放行,false 已超限应拒绝
     */
    boolean tryAcquire(String key, int limit, long windowMillis);
}
