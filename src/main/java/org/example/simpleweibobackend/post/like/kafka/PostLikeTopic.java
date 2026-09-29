package org.example.simpleweibobackend.post.like.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** 声明 Kafka topic */
@Configuration
public class PostLikeTopic {

    public static final String TOPIC = "post-likes";

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name(TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
