package org.example.simpleweibobackend.post.feed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

// Feed fanout线程池：发帖后本地异步扩散写收件箱（替代原MQ消费模式）
@Configuration
public class FeedFanoutExecutorConfig {

    @Bean
    public ThreadPoolTaskExecutor feedFanoutExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("feed-fanout-");
        // 队列打满时由发帖线程自己执行：天然背压，不丢扩散任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
