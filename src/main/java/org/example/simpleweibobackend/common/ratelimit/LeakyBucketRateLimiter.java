package org.example.simpleweibobackend.common.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class LeakyBucketRateLimiter implements RateLimiter {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final class Bucket {
        private double water;
        private long lastLeakTime;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        double leakPerMillis = (double) limit / windowMillis;
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket());
        synchronized (bucket) {
            if (bucket.lastLeakTime == 0) {
                bucket.lastLeakTime = now;
            } else {
                bucket.water = Math.max(0, bucket.water - (now - bucket.lastLeakTime) * leakPerMillis);
                bucket.lastLeakTime = now;
            }
            if (bucket.water + 1 > limit) {
                return false;
            }
            bucket.water++;
            return true;
        }
    }
}
