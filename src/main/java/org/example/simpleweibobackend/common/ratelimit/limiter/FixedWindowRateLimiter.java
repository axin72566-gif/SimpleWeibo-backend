package org.example.simpleweibobackend.common.ratelimit.limiter;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 固定窗口限流:每个窗口(如每分钟)一个计数器,窗口到期清零。
 * 简单高效,但窗口边界处可能瞬间放过 2 倍流量(上一窗口末尾 + 下一窗口开头)
 */
@Component
public class FixedWindowRateLimiter implements RateLimiter {

    /**
     * 每个 key 一份独立的计数状态
     */
    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    private static final class Window {
        /**
         * 当前窗口的起点时间戳(毫秒)
         */
        private long start;
        /**
         * 当前窗口内已放行的请求数
         */
        private int count;
    }

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        Window window = windows.computeIfAbsent(key, k -> new Window());
        // 锁粒度是单个 key,不同 key 之间互不阻塞
        synchronized (window) {
            // 距窗口起点已超过一个窗口周期,说明旧窗口过期,重新计数
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
