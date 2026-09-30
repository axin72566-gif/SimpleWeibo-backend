package org.example.simpleweibobackend.post.feed.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** 声明 Kafka topic */
@Configuration
public class FeedPushTopic {

    public static final String TOPIC = "feed-push";

    @Bean
    public NewTopic feedTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
