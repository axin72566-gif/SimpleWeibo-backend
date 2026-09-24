package org.example.simpleweibobackend.common.exception;

import lombok.Getter;
import org.example.simpleweibobackend.common.ErrorCode;

/**
 * 业务异常:携带错误码枚举,由 {@link GlobalExceptionHandler} 统一转为 Result 响应
 */
@Getter
public class BizException extends RuntimeException {

    /**
     * 错误码枚举,提供业务码与 HTTP 状态码
     */
    private final ErrorCode errorCode;

    /**
     * @param errorCode 错误码枚举
     * @param message   具体的错误描述
     */
    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
