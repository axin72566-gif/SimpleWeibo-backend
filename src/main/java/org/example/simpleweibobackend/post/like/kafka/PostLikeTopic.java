package org.example.simpleweibobackend.post.like.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/** 声明 Kafka topic,启动时不存在则自动创建 */
@Configuration
public class PostLikeTopic {

    @Bean
    public NewTopic likeTopic() {
        return TopicBuilder.name("post-likes")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
