package org.example.simpleweibobackend.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.Result;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * 全局异常处理器:继承 {@link ResponseEntityExceptionHandler} 接管 Spring MVC 标准异常
 * (方法不支持、媒体类型、参数绑定、JSON 解析失败等,按框架判定的状态码返回),
 * 连同业务异常、未预期异常一起统一包装为 {@link Result} 响应
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 业务异常:HTTP 状态码取错误码枚举的 httpStatus,业务码放响应体,日志级别 warn
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        log.warn("业务异常: {}", e.getMessage());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(Result.fail(e.getErrorCode().getCode(), e.getMessage()));
    }

    /**
     * 未预期的系统异常:统一按 500 返回,不向前端暴露堆栈,日志级别 error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleUnexpectedException(Exception e) {
        log.error("系统异常", e);
        return ResponseEntity.status(ErrorCode.INTERNAL_ERROR.getHttpStatus())
                .body(Result.fail(ErrorCode.INTERNAL_ERROR));
    }

    /**
     * 参数校验失败(@Valid):返回第一条字段校验消息,如"帖子标题不能为空"
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse(ErrorCode.BAD_REQUEST.getMessage());
        return ResponseEntity.status(status)
                .body(Result.fail(ErrorCode.BAD_REQUEST.getCode(), message));
    }

    /**
     * 其余 MVC 标准异常的统一出口:按框架判定的状态码返回,包装为 Result
     */
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
                                                             Object body,
                                                             @NonNull HttpHeaders headers,
                                                             @NonNull HttpStatusCode statusCode,
                                                             @NonNull WebRequest request) {
        String message = ex.getMessage() != null ? ex.getMessage() : "请求处理失败";
        return ResponseEntity.status(statusCode)
                .headers(headers)
                .body(Result.fail(statusCode.value(), message));
    }
}
