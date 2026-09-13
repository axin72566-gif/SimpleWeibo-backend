package org.example.simpleweibobackend.common.ratelimit;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class FixedWindowRateLimiter implements RateLimiter {

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    private static final class Window {
        private long start;
        private int count;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(key, k -> new Window());
        synchronized (window) {
            if (now - window.start >= windowMillis) {
                window.start = now;
                window.count = 0;
            }
            if (window.count >= limit) {
                return false;
            }
            window.count++;
            return true;
        }
    }
}
