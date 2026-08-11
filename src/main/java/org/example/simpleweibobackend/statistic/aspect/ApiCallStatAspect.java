package org.example.simpleweibobackend.statistic.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.simpleweibobackend.statistic.buffer.ApiCallStatBuffer;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiCallStatAspect {

    private final ApiCallStatBuffer apiCallStatBuffer;

    @Before("execution(* org.example.simpleweibobackend..controller..*.*(..)) && " +
            "!within(org.example.simpleweibobackend.statistic..*)")
    public void countApiCall(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String httpMethod = request.getMethod();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String controllerClass = signature.getDeclaringType().getSimpleName();
            String controllerMethod = signature.getName();

            String apiPath = resolveApiPath(request, controllerClass, controllerMethod);

            apiCallStatBuffer.increment(apiPath, httpMethod, controllerClass, controllerMethod);
        } catch (Exception e) {
            log.warn("记录接口调用统计失败", e);
        }
    }

    private String resolveApiPath(HttpServletRequest request,
                                  String controllerClass, String controllerMethod) {
        Object pattern = request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        if (pattern != null) {
            return pattern.toString();
        }
        // 降级：用 控制器类名#方法名 替代原始 URI，确保 buffer 条目数始终有界
        return controllerClass + "#" + controllerMethod;
    }
}
