package org.example.simpleweibobackend.ratelimit.limiter;

import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 漏桶限流:请求像水一样注入桶中,桶以恒定速率(每 windowMillis 毫秒漏掉 limit 单位)漏水,
 * 桶容量为 limit;水满后再来的请求直接拒绝。
 * 与令牌桶相反,漏桶不允许突发,输出速率恒定平滑,适合保护处理能力有限的下游
 */
@Component
public class LeakyBucketRateLimiter implements RateLimiter {

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.LEAKY_BUCKET;
    }

    /**
     * 每个 key 一个独立的漏桶
     */
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final class Bucket {
        /**
         * 当前桶中的水量(懒计算:每次请求时按时间差先漏水)
         */
        private double water;
        /**
         * 上次漏水的时间戳
         */
        private long lastLeakTime;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        // 漏水速率 = limit 单位水 / windowMillis 毫秒
        double leakPerMillis = (double) limit / windowMillis;
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());
        // 锁粒度是单个 key,不同 key 之间互不阻塞
        synchronized (bucket) {
            if (bucket.lastLeakTime == 0) {
                // 首次请求:只记录起点,水量从 0 开始
                bucket.lastLeakTime = now;
            } else {
                // 按距上次漏水的时间差先漏掉一部分水,最低漏到 0
                bucket.water = Math.max(0, bucket.water - (now - bucket.lastLeakTime) * leakPerMillis);
                bucket.lastLeakTime = now;
            }
            // 加上本次请求的水量后若超过桶容量则拒绝
            if (bucket.water + 1 > limit) {
                return false;
            }
            bucket.water++;
            return true;
        }
    }
}
