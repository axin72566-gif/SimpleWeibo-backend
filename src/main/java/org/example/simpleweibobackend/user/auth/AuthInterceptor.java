package org.example.simpleweibobackend.user.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 登录态校验 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        String authorization = request.getHeader("Authorization");
        String token = (authorization != null && authorization.startsWith(BEARER_PREFIX))
                ? authorization.substring(BEARER_PREFIX.length()).trim() : null;
        String userId = null;
        try {
            userId = (token == null || token.isEmpty()) ? null
                    : stringRedisTemplate.opsForValue().get(UserContext.LOGIN_TOKEN + token);
        } catch (Exception e) {
            log.error("登录态校验失败: token 不存在或无效, token={}", token, e);
        }
        if (userId == null) {
            log.warn("登录态校验失败: token 不存在或无效");
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        UserContext.set(Long.valueOf(userId), token);
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}