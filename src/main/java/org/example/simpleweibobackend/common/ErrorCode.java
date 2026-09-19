package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码枚举:统一业务异常与 HTTP 状态码,
 * {@link Result} 与 {@link org.example.simpleweibobackend.common.exception.GlobalExceptionHandler} 使用
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源已存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    AUDIT_REJECTED(1001, "内容未通过审核");

    /**
     * 错误码,同时作为 HTTP 响应状态码
     */
    private final Integer code;

    /**
     * 对外展示的错误信息
     */
    private final String message;
}
