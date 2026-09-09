package org.example.simpleweibobackend.post.like;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name("like-topic")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
