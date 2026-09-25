package org.example.simpleweibobackend.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局错误码:code 返回在响应体 Result.code,httpStatus 在 code 超出标准 HTTP 范围(100-599)时单独指定响应状态
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    BAD_REQUEST(400, 400, "请求参数错误"),
    NOT_FOUND(404, 404, "资源不存在"),
    CONFLICT(409, 409, "资源已存在"),
    INTERNAL_ERROR(500, 500, "服务器内部错误"),
    AUDIT_REJECTED(1001, 400, "内容未通过审核");

    private final Integer code;

    private final Integer httpStatus;

    private final String message;
}
