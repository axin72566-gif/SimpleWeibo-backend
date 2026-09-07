package org.example.simpleweibobackend.common;

import org.example.simpleweibobackend.common.exception.BizException;
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
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求不能为空");
        }

        String header = attributes.getRequest().getHeader(USER_ID_HEADER);
        if (header == null) {
            throw new BizException(ErrorCode.BAD_REQUEST, "请求头中未指定用户ID");
        }

        return Long.valueOf(header);
    }
}
