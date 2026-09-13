package org.example.simpleweibobackend.common.ratelimit;

public interface RateLimiter {

    boolean tryAcquire(String key, int limit, long windowMillis);
}
