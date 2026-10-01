package org.example.simpleweibobackend.ratelimit.limiter;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/** 滑动窗口限流: ZSET 记录窗口内每个请求的时间戳, 先清理窗口外请求再统计当前窗口内的请求数 */
@Component
@RequiredArgsConstructor
public class SlidingWindowRateLimiter implements RateLimiter {

    private static final RedisScript<Long> SLIDING_WINDOW_SCRIPT = new DefaultRedisScript<>("""
            local now = tonumber(ARGV[1])
            local window = tonumber(ARGV[2])
            local limit = tonumber(ARGV[3])
            redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, now - window)
            if redis.call('ZCARD', KEYS[1]) >= limit then
                return 0
            end
            redis.call('ZADD', KEYS[1], now, ARGV[4])
            redis.call('PEXPIRE', KEYS[1], window + 1000)
            return 1
            """, Long.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.SLIDING_WINDOW;
    }

    @Override
    public boolean tryAcquire(String key, long limit, Duration window) {
        long now = System.currentTimeMillis();
        // 请求唯一标识, 避免同一毫秒内的请求在 ZSET 中互相覆盖
        String member = now + ":" + UUID.randomUUID();
        Long allowed = stringRedisTemplate.execute(SLIDING_WINDOW_SCRIPT, List.of(key),
                String.valueOf(now), String.valueOf(window.toMillis()), String.valueOf(limit), member);
        return Long.valueOf(1L).equals(allowed);
    }
}
