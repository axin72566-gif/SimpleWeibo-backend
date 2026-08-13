package org.example.simpleweibobackend.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.user.annotation.RequireRole;
import org.example.simpleweibobackend.user.constant.Role;
import org.example.simpleweibobackend.util.JwtUtil;
import org.example.simpleweibobackend.util.UserContext;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    private static final String UNAUTHORIZED_JSON = "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}";
    private static final String FORBIDDEN_JSON = "{\"code\":403,\"message\":\"无权限访问\",\"data\":null}";

    @Override
    public boolean preHandle(HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (jwtUtil.isBlacklisted(token)) {
                writeUnauthorized(response);
                return false;
            }
            UserContext.setUserId(jwtUtil.parseUserId(token));
            UserContext.setRole(jwtUtil.parseRole(token));
            return checkRole(handler, response);
        } catch (Exception e) {
            writeUnauthorized(response);
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(UNAUTHORIZED_JSON);
    }

    private boolean checkRole(Object handler, HttpServletResponse response) throws Exception {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = handlerMethod.getBeanType().getAnnotation(RequireRole.class);
        }
        if (requireRole == null) {
            return true;
        }
        Role currentRole = UserContext.getRole();
        for (Role required : requireRole.value()) {
            if (required == currentRole) {
                return true;
            }
        }
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(FORBIDDEN_JSON);
        return false;
    }
}
