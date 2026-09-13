package org.example.simpleweibobackend.common.ratelimit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.ratelimit.limiter.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 限流切面:拦截标注了 {@link RateLimit} 的方法,
 * 组装限流 key 后调用对应算法的 {@link RateLimiter#tryAcquire},
 * 被拒绝时抛出 429 业务异常,由全局异常处理器统一返回
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitAspect {

    /**
     * 用户级限流从该请求头取用户标识,与项目其他接口保持一致
     */
    private static final String USER_ID_HEADER = "X-User-Id";

    private final RateLimiterRegistry registry;

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = buildKey(joinPoint, rateLimit);
        boolean allowed = registry.get(rateLimit.algorithm())
                .tryAcquire(key, rateLimit.limit(), rateLimit.window() * 1000L);
        if (!allowed) {
            log.warn("触发限流, key: {}, algorithm: {}", key, rateLimit.algorithm());
            throw new BizException(ErrorCode.RATE_LIMITED, rateLimit.message());
        }
        return joinPoint.proceed();
    }

    /**
     * 组装限流 key,格式:rate_limit:{类名#方法名}[:{维度标识}]
     * 接口级:rate_limit:PostQueryController#getPostById
     * IP 级:rate_limit:RegisterController#register:192.168.1.1
     */
    private String buildKey(ProceedingJoinPoint joinPoint, RateLimit rateLimit) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String method = signature.getDeclaringType().getSimpleName() + "#" + signature.getName();
        String prefix = "rate_limit:" + method;
        return switch (rateLimit.dimension()) {
            case INTERFACE -> prefix;
            case USER -> prefix + ":" + resolveUserId();
            case IP -> prefix + ":" + resolveIp();
        };
    }

    /**
     * 取用户标识:优先 X-User-Id 请求头,未登录时退化为 IP
     */
    private String resolveUserId() {
        ServletRequestAttributes attributes = currentAttributes();
        if (attributes == null) {
            return "anonymous";
        }
        String userId = attributes.getRequest().getHeader(USER_ID_HEADER);
        return userId == null || userId.isBlank() ? resolveIp() : userId;
    }

    /**
     * 取客户端 IP
     */
    private String resolveIp() {
        ServletRequestAttributes attributes = currentAttributes();
        return attributes == null ? "unknown" : attributes.getRequest().getRemoteAddr();
    }

    private ServletRequestAttributes currentAttributes() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }
}
