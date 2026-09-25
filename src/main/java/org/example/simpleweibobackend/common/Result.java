package org.example.simpleweibobackend.common;

import lombok.Getter;

/**
 * 统一响应体:所有接口返回 {code, message, data} 结构
 */
@Getter
public class Result<T> {

    private static final Integer SUCCESS_CODE = 200;

    private static final String SUCCESS_MESSAGE = "成功";

    private final Integer code;

    private final String message;

    private final T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
