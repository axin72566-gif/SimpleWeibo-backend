package org.example.simpleweibobackend.common.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

/**
 * 开发联调用的用户身份，由调用方通过请求头指定，不代表已认证用户。
 */
public final class UserContext {

    public static final String USER_ID_HEADER = "X-User-Id";

    private UserContext() {
    }

    public static Long getUserId() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            throw new IllegalStateException("当前线程没有 HTTP 请求，无法获取用户 ID");
        }

        String value = attributes.getRequest().getHeader(USER_ID_HEADER);
        if (value == null || value.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "缺少 X-User-Id 请求头");
        }

        if (value.matches("[0-9]+")) {
            try {
                long userId = Long.parseLong(value);
                if (userId > 0) {
                    return userId;
                }
            } catch (NumberFormatException ignored) {
                // 超出 Long 范围的输入按非法用户 ID 处理。
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "X-User-Id 必须是 Long 范围内的正整数");
    }
}
