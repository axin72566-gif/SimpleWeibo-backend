package org.example.simpleweibobackend.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.RequireRole;
import org.example.simpleweibobackend.common.Role;
import org.example.simpleweibobackend.util.JwtUtil;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final TokenBlacklist tokenBlacklist;

    private static final String UNAUTHORIZED_JSON = "{\"code\":401,\"message\":\"\u672a\u767b\u5f55\u6216\u767b\u5f55\u5df2\u8fc7\u671f\",\"data\":null}";
    private static final String FORBIDDEN_JSON = "{\"code\":403,\"message\":\"\u65e0\u6743\u9650\u8bbf\u95ee\",\"data\":null}";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            writeUnauthorized(response);
            return false;
        }

        String token = authHeader.substring(7);
        try {
            if (tokenBlacklist.contains(token)) {
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
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
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
