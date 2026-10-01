package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    PARAM_ERROR(40000, 400, "请求参数错误"),
    CANNOT_FOLLOW_SELF(40001, 400, "不能关注自己"),
    UNAUTHORIZED(40100, 401, "未登录或登录已过期"),
    LOGIN_FAILED(40101, 401, "用户名或密码错误"),
    USER_NOT_FOUND(40400, 404, "用户不存在"),
    POST_NOT_FOUND(40401, 404, "帖子不存在"),
    LIKE_NOT_FOUND(40402, 404, "尚未点赞"),
    FOLLOW_NOT_FOUND(40403, 404, "尚未关注"),
    USERNAME_EXISTS(40900, 409, "用户名已存在"),
    REPEAT_LIKE(40901, 409, "请勿重复点赞"),
    REPEAT_FOLLOW(40902, 409, "请勿重复关注"),
    AUDIT_REJECTED(42200, 422, "内容未通过审核"),
    RATE_LIMITED(42900, 429, "请求过于频繁,请稍后再试"),
    INTERNAL_ERROR(50000, 500, "服务器内部错误"),
    LOGOUT_FAILED(50001, 500, "退出登录失败");

    private final Integer code;

    private final Integer httpStatus;

    private final String message;
}
