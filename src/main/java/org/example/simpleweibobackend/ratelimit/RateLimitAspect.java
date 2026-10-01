package org.example.simpleweibobackend.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.ratelimit.limiter.RateLimiter;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.stereotype.Component;

import java.time.Duration;

/** {@link RateLimit} 注解切面: 拦截标注方法, 以 userId + 业务 key 为维度调用限流算法 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    private final RateLimiterRegistry rateLimiterRegistry;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            log.warn("限流失败, 接口未登录: 限流以 userId 为维度, 标注 @RateLimit 的接口必须处于登录态");
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        long windowMillis = rateLimit.timeUnit().toMillis(rateLimit.window());
        if (rateLimit.key().isBlank() || rateLimit.limit() < 1 || windowMillis < 1) {
            throw new IllegalArgumentException("RateLimit 参数非法: key=" + rateLimit.key()
                    + ", limit=" + rateLimit.limit() + ", windowMillis=" + windowMillis);
        }
        RateLimiter limiter = rateLimiterRegistry.get(rateLimit.algorithm());
        String key = RateLimitRedisKey.RATE_LIMIT + rateLimit.key() + ":" + userId;
        boolean allowed;
        try {
            allowed = limiter.tryAcquire(key, rateLimit.limit(), Duration.ofMillis(windowMillis));
        } catch (Exception e) {
            // Redis 等基础设施故障时降级放行, 限流组件自身不阻断业务
            log.error("限流执行异常, 放行请求: key={}, algorithm={}", key, rateLimit.algorithm(), e);
            return joinPoint.proceed();
        }
        if (!allowed) {
            log.info("触发限流: key={}, algorithm={}, limit={}, windowMillis={}",
                    key, rateLimit.algorithm(), rateLimit.limit(), windowMillis);
            throw new BizException(ErrorCode.RATE_LIMITED);
        }
        return joinPoint.proceed();
    }
}
