package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码:code 为 5 位业务码(40xxx 客户端错误 / 50xxx 服务端错误),返回在响应体 Result.code;
 * httpStatus 独立决定 HTTP 响应状态码,仅做传输层映射
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    PARAM_ERROR(40000, 400, "请求参数错误"),
    UNAUTHORIZED(40100, 401, "未登录或登录已过期"),
    LOGIN_FAILED(40101, 401, "用户名或密码错误"),
    USER_NOT_FOUND(40400, 404, "用户不存在"),
    POST_NOT_FOUND(40401, 404, "帖子不存在"),
    USERNAME_EXISTS(40900, 409, "用户名已存在"),
    AUDIT_REJECTED(42200, 400, "内容未通过审核"),
    INTERNAL_ERROR(50000, 500, "服务器内部错误");

    private final Integer code;

    private final Integer httpStatus;

    private final String message;
}
