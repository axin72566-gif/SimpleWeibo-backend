package org.example.simpleweibobackend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器:把异常统一转换为 {@link Result} JSON 响应,
 * 避免堆栈信息暴露给前端
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常:使用异常自带的错误码和信息返回,日志级别 warn
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.status(e.getCode())
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    /**
     * 未预期的系统异常:统一按 500 返回,不向前端暴露堆栈,日志级别 error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getCode())
                .body(Result.fail(ErrorCode.INTERNAL_ERROR));
    }
}
