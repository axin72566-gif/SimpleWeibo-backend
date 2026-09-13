package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源已存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    AUDIT_REJECTED(1001, "内容未通过审核"),
    RATE_LIMITED(429, "请求过于频繁");

    private final Integer code;

    private final String message;
}
