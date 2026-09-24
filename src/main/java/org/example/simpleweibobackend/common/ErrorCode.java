package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码枚举:业务码返回在响应体 {@code Result.code},HTTP 状态码单独指定,
 * {@link Result} 与 {@link org.example.simpleweibobackend.common.exception.GlobalExceptionHandler} 使用
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(400, 400, "请求参数错误"),
    NOT_FOUND(404, 404, "资源不存在"),
    CONFLICT(409, 409, "资源已存在"),
    INTERNAL_ERROR(500, 500, "服务器内部错误"),
    AUDIT_REJECTED(1001, 400, "内容未通过审核");

    /**
     * 业务错误码,返回在响应体 Result.code 中
     */
    private final Integer code;

    /**
     * HTTP 响应状态码,业务码不在标准范围(100-599)时以此为准
     */
    private final Integer httpStatus;

    /**
     * 对外展示的错误信息
     */
    private final String message;
}
