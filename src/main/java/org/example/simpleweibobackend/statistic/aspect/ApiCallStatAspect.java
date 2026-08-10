package org.example.simpleweibobackend.statistic.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ApiCallStatAspect {

    private final ApiCallStatService apiCallStatService;

    @Before("execution(* org.example.simpleweibobackend..controller..*.*(..))")
    public void countApiCall(JoinPoint joinPoint) {
        try {
            HttpServletRequest request = ((ServletRequestAttributes)
                    RequestContextHolder.currentRequestAttributes()).getRequest();
            String httpMethod = request.getMethod();
            String apiPath = request.getRequestURI();

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String controllerClass = signature.getDeclaringType().getSimpleName();
            String controllerMethod = signature.getName();

            apiCallStatService.recordCall(apiPath, httpMethod, controllerClass, controllerMethod);
        } catch (Exception e) {
            log.warn("记录接口调用统计失败", e);
        }
    }
}
