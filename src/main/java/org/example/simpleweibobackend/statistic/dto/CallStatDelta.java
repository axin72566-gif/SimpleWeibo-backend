package org.example.simpleweibobackend.statistic.dto;

import java.time.LocalDateTime;

public record CallStatDelta(String apiPath, String httpMethod,
                            String controllerClass, String controllerMethod,
                            long count, LocalDateTime lastCallTime) {
}
