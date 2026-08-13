package org.example.simpleweibobackend.statistic.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.statistic.entity.ApiCallStat;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "接口调用统计")
public class ApiCallStatVO {

    @Schema(description = "记录ID")
    private Long id;

    @Schema(description = "接口路径")
    private String apiPath;

    @Schema(description = "HTTP方法")
    private String httpMethod;

    @Schema(description = "Controller类名")
    private String controllerClass;

    @Schema(description = "Controller方法名")
    private String controllerMethod;

    @Schema(description = "调用次数")
    private Long callCount;

    @Schema(description = "最后调用时间")
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
