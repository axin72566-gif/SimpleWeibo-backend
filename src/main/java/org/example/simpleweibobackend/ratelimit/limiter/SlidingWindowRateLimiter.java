package org.example.simpleweibobackend.ratelimit.limiter;

import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 滑动窗口限流:为每个 key 记录窗口内每次放行请求的精确时间戳,
 * 判定时先剔除滑出窗口的旧时间戳,再用剩余条数与阈值比较。
 * 窗口起点随当前时间滑动,不存在固定窗口的边界突刺问题
 */
@Component
public class SlidingWindowRateLimiter implements RateLimiter {

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.SLIDING_WINDOW;
    }

    /**
     * 每个 key 一个时间戳队列:队头是最老请求,队尾是最新请求
     */
    private final ConcurrentHashMap<String, Deque<Long>> windows = new ConcurrentHashMap<>();

    @Override
    public boolean tryAcquire(String key, int limit, long windowMillis) {
        long now = System.currentTimeMillis();
        Deque<Long> deque = windows.computeIfAbsent(key, k -> new ArrayDeque<>());
        // 锁粒度是单个 key,不同 key 之间互不阻塞
        synchronized (deque) {
            // 清理已滑出窗口的过期时间戳(早于 now - windowMillis 的记录不再计数)
            while (!deque.isEmpty() && now - deque.peekFirst() >= windowMillis) {
                deque.pollFirst();
            }
            // 窗口内剩余条数即最近一段时间的真实请求数
            if (deque.size() >= limit) {
                return false;
            }
            // 记录本次放行请求的时间戳,供后续请求统计
            deque.addLast(now);
            return true;
        }
    }
}
