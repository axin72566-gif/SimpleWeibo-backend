package org.example.simpleweibobackend.ratelimit.limiter;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

/**
 * 令牌桶限流: 桶容量为 limit, 按 limit/window 的速率惰性补充令牌(每次访问时按流逝时长补足),
 * 平稳期等价于 limit/window 的匀速限流, 空闲后恢复的突发容量最高为 limit
 */
@Component
@RequiredArgsConstructor
public class TokenBucketRateLimiter implements RateLimiter {

    private static final RedisScript<Long> TOKEN_BUCKET_SCRIPT = new DefaultRedisScript<>("""
            local capacity = tonumber(ARGV[1])
            local rate = tonumber(ARGV[2])
            local now = tonumber(ARGV[3])
            local bucket = redis.call('HMGET', KEYS[1], 'tokens', 'ts')
            local tokens = tonumber(bucket[1])
            local ts = tonumber(bucket[2])
            if tokens == nil then
                tokens = capacity
                ts = now
            end
            tokens = math.min(capacity, tokens + math.max(0, now - ts) * rate)
            local allowed = 0
            if tokens >= 1 then
                tokens = tokens - 1
                allowed = 1
            end
            redis.call('HSET', KEYS[1], 'tokens', string.format('%.6f', tokens), 'ts', now)
            redis.call('PEXPIRE', KEYS[1], ARGV[4])
            return allowed
            """, Long.class);

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public RateLimitAlgorithm algorithm() {
        return RateLimitAlgorithm.TOKEN_BUCKET;
    }

    @Override
    public boolean tryAcquire(String key, long limit, Duration window) {
        long windowMillis = window.toMillis();
        // 每毫秒补充的令牌数; 过期时间取加满一桶所需的 window 再留 1 秒余量,
        // 键过期后重新初始化为满桶, 与真实补满的语义等价
        String refillRatePerMillis = String.valueOf((double) limit / windowMillis);
        Long allowed = stringRedisTemplate.execute(TOKEN_BUCKET_SCRIPT, List.of(key),
                String.valueOf(limit), refillRatePerMillis,
                String.valueOf(System.currentTimeMillis()), String.valueOf(windowMillis + 1000));
        return Long.valueOf(1L).equals(allowed);
    }
}
