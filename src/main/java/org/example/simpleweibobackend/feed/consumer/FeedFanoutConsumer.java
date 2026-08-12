package org.example.simpleweibobackend.feed.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.feed.config.FeedMqConfig;
import org.example.simpleweibobackend.feed.dto.PostCreatedEvent;
import org.example.simpleweibobackend.feed.service.FeedService;
import org.example.simpleweibobackend.post.entity.Post;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedFanoutConsumer {

    private final FeedService feedService;

    @RabbitListener(queues = FeedMqConfig.QUEUE)
    public void onPostCreated(PostCreatedEvent event) {
        log.info("收到发帖事件: postId={}, userId={}", event.getPostId(), event.getUserId());
        Post post = new Post();
        post.setId(event.getPostId());
        post.setUserId(event.getUserId());
        feedService.fanout(post);
    }
}
