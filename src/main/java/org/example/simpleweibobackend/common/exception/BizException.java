package org.example.simpleweibobackend.common.exception;

import lombok.Getter;
import org.example.simpleweibobackend.common.ErrorCode;

/**
 * 业务异常:携带 ErrorCode,由 GlobalExceptionHandler 统一转为 Result 响应
 */
@Getter
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
