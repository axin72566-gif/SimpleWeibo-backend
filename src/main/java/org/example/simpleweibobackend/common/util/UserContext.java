package org.example.simpleweibobackend.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 开发联调用的用户身份，由调用方通过请求头指定，不代表已认证用户。
 */
public final class UserContext {

    public static final String USER_ID_HEADER = "X-User-Id";

    private UserContext() {
    }

    public static Long getUserId() {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return Long.valueOf(attributes.getRequest().getHeader(USER_ID_HEADER));
    }
}
