package org.example.simpleweibobackend.statistic.buffer;

import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.statistic.dto.CallStatDelta;
import org.example.simpleweibobackend.statistic.service.ApiCallStatService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiCallStatBuffer {

    private final ApiCallStatService apiCallStatService;

    private final ConcurrentHashMap<String, CallCounter> buffer = new ConcurrentHashMap<>();

    private static class CallCounter {
        final LongAdder count = new LongAdder();
        final String apiPath;
        final String httpMethod;
        volatile String controllerClass;
        volatile String controllerMethod;
        final AtomicLong lastCallTime = new AtomicLong();

        CallCounter(String apiPath, String httpMethod) {
            this.apiPath = apiPath;
            this.httpMethod = httpMethod;
        }
    }

    public void increment(String apiPath, String httpMethod,
                          String controllerClass, String controllerMethod) {
        String key = httpMethod + " " + apiPath;
        CallCounter counter = buffer.computeIfAbsent(key, k -> new CallCounter(apiPath, httpMethod));
        counter.count.increment();
        counter.controllerClass = controllerClass;
        counter.controllerMethod = controllerMethod;
        counter.lastCallTime.set(System.currentTimeMillis());
    }

    @Scheduled(fixedRate = 10000)
    public void flush() {
        List<CallStatDelta> deltas = drain();
        if (deltas.isEmpty()) {
            return;
        }
        try {
            apiCallStatService.flushBatch(deltas);
        } catch (Exception e) {
            log.warn("批量刷写接口调用统计失败，本批 {} 条数据丢失", deltas.size(), e);
        }
    }

    private List<CallStatDelta> drain() {
        List<CallStatDelta> deltas = new ArrayList<>();
        buffer.forEach((key, counter) -> {
            long delta = counter.count.sumThenReset();
            if (delta > 0) {
                LocalDateTime lastCallTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(counter.lastCallTime.get()), ZoneId.systemDefault());
                deltas.add(new CallStatDelta(
                        counter.apiPath, counter.httpMethod,
                        counter.controllerClass, counter.controllerMethod,
                        delta, lastCallTime));
            }
        });
        return deltas;
    }

    @PreDestroy
    public void onShutdown() {
        log.info("应用关闭，刷写剩余接口调用统计数据...");
        flush();
    }
}
