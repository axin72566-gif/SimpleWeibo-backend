package org.example.simpleweibobackend.statistic.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApiCallStatVO {

    private Long id;

    private String apiPath;

    private String httpMethod;

    private String controllerClass;

    private String controllerMethod;

    private Long callCount;

    private LocalDateTime lastCallTime;

    public static ApiCallStatVO from(ApiCallStat stat) {
        return new ApiCallStatVO(
                stat.getId(),
                stat.getApiPath(),
                stat.getHttpMethod(),
                stat.getControllerClass(),
                stat.getControllerMethod(),
                stat.getCallCount(),
                stat.getLastCallTime()
        );
    }
}
