package org.example.simpleweibobackend.ratelimit.limiter;

import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 令牌桶限流:系统以恒定速率(每 windowMillis 毫秒补充 limit 个)往桶里放令牌,
 * 桶容量为 limit;请求到来时取走 1 个令牌,取不到则拒绝。
 * 空闲期积攒的令牌允许突发流量一次性消耗,长期平均速率被卡在 limit/window
 */
@Component
public class TokenBucketRateLimiter implements RateLimiter {

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.TOKEN_BUCKET;
    }

    /**
     * 每个 key 一个独立的令牌桶
     */
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final class Bucket {
        /**
         * 当前剩余令牌数(懒计算:不做定时任务,而是在每次请求时按时间差补充)
         */
        private double tokens;
        /**
         * 上次补充令牌的时间戳
         */
        private long lastRefillTime;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        // 补充速率 = limit 个令牌 / windowMillis 毫秒
        double refillPerMillis = (double) limit / windowMillis;
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());
        // 锁粒度是单个 key,不同 key 之间互不阻塞
        synchronized (bucket) {
            if (bucket.lastRefillTime == 0) {
                // 首次请求:桶满初始化,允许一次完整额度的突发
                bucket.tokens = limit;
            } else {
                // 按距上次补充的时间差补充令牌,上限为桶容量
                bucket.tokens = Math.min(limit, bucket.tokens + (now - bucket.lastRefillTime) * refillPerMillis);
            }
            bucket.lastRefillTime = now;
            if (bucket.tokens < 1) {
                return false;
            }
            bucket.tokens--;
            return true;
        }
    }
}
