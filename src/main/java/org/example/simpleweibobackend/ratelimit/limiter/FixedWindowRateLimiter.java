package org.example.simpleweibobackend.ratelimit.limiter;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.example.simpleweibobackend.ratelimit.RateLimiter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/** 固定窗口限流: INCR 计数, 首个请求开窗并设置过期, 窗口内计数超过阈值即拒绝 */
@Component
@RequiredArgsConstructor
public class FixedWindowRateLimiter implements RateLimiter {

    private static final RedisScript<Long> FIXED_WINDOW_SCRIPT = new DefaultRedisScript<>("""
            local count = redis.call('INCR', KEYS[1])
            if count == 1 then
                redis.call('PEXPIRE', KEYS[1], ARGV[1])
            end
            if count > tonumber(ARGV[2]) then
                return 0
            end
            return 1
            """, Long.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.FIXED_WINDOW;
    }

    @Override
    public boolean tryAcquire(String key, long limit, Duration window) {
        Long allowed = stringRedisTemplate.execute(FIXED_WINDOW_SCRIPT,
                List.of(key), String.valueOf(window.toMillis()), String.valueOf(limit));
        return Long.valueOf(1L).equals(allowed);
    }
}
