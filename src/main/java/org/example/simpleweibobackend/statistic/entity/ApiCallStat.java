package org.example.simpleweibobackend.statistic.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("api_call_stat")
public class ApiCallStat extends BaseEntity {

    private String apiPath;

    private String httpMethod;

    private String controllerClass;

    private String controllerMethod;

    private Long callCount;

    private LocalDateTime lastCallTime;
}
