package org.example.simpleweibobackend.common.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class TokenBucketRateLimiter implements RateLimiter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final class Bucket {
        private double tokens;
        private long lastRefillTime;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        double refillPerMillis = (double) limit / windowMillis;
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());
        synchronized (bucket) {
            if (bucket.lastRefillTime == 0) {
                bucket.tokens = limit;
            } else {
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
