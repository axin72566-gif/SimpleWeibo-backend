package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** 全局错误码 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    PARAM_ERROR(40000, 400, "请求参数错误"),
    UNAUTHORIZED(40100, 401, "未登录或登录已过期"),
    LOGIN_FAILED(40101, 401, "用户名或密码错误"),
    USER_NOT_FOUND(40400, 404, "用户不存在"),
    POST_NOT_FOUND(40401, 404, "帖子不存在"),
    USERNAME_EXISTS(40900, 409, "用户名已存在"),
    REPEAT_LIKE(40901, 409, "请勿重复点赞"),
    AUDIT_REJECTED(42200, 400, "内容未通过审核"),
    INTERNAL_ERROR(50000, 500, "服务器内部错误"),
    LOGOUT_FAILED(50001, 500, "退出登录失败");

    private final Integer code;

    private final Integer httpStatus;

    private final String message;
}
