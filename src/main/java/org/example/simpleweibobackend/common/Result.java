package org.example.simpleweibobackend.common;

import lombok.Getter;

/**
 * 统一响应体:所有接口返回 {code, message, data} 结构
 */
@Getter
public class Result<T> {

    /**
     * 成功业务码
     */
    private static final Integer SUCCESS_CODE = 200;

    /**
     * 成功响应信息
     */
    private static final String SUCCESS_MESSAGE = "成功";

    /**
     * 响应码,200 表示成功,失败取值见 {@link ErrorCode}
     */
    private final Integer code;

    /**
     * 响应信息
     */
    private final String message;

    /**
     * 业务数据,失败时为 null
     */
    private final T data;

    private Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构建成功响应,data 为业务返回值
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    /**
     * 按错误码枚举构建失败响应
     */
    public static <T> Result<T> fail(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    /**
     * 按自定义错误码和信息构建失败响应
     */
    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message, null);
    }
}
