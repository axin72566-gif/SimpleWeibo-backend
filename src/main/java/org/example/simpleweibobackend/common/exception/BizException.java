package org.example.simpleweibobackend.common.exception;

import lombok.Getter;
import org.example.simpleweibobackend.common.ErrorCode;

@Getter
public class BizException extends RuntimeException {

    private final Integer code;

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }
}
