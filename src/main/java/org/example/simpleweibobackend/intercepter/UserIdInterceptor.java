package org.example.simpleweibobackend.intercepter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.simpleweibobackend.util.UserContext;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class UserIdInterceptor implements HandlerInterceptor {

    public static final String USER_ID_HEADER = "X-User-Id";

    private static final String BAD_REQUEST_JSON = "{\"code\":400,\"message\":\"X-User-Id 请求头格式错误，应为数字用户ID\",\"data\":null}";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            // 未携带用户ID时放行，无需用户身份的接口（注册、浏览等）可正常访问
            return true;
        }
        try {
            UserContext.setUserId(Long.valueOf(userId));
            return true;
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(BAD_REQUEST_JSON);
            return false;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        UserContext.clear();
    }
}
