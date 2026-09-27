package org.example.simpleweibobackend.common.exception;

import lombok.Getter;
import org.example.simpleweibobackend.common.ErrorCode;

/**
 * 业务异常:携带 ErrorCode,由 GlobalExceptionHandler 统一转为 Result 响应;
 * 提示文案与错误码默认文案一致时用单参构造,需要补充上下文(如资源ID)时用双参构造
 */
@Getter
public class BizException extends RuntimeException {

    private final ErrorCode errorCode;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
}
