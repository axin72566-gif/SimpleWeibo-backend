package org.example.simpleweibobackend.post.feed.outbox.relay;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.feed.config.FeedMqConfig;
import org.example.simpleweibobackend.post.feed.dto.PostCreatedEvent;
import org.example.simpleweibobackend.post.feed.outbox.entity.Outbox;
import org.example.simpleweibobackend.post.feed.outbox.mapper.OutboxMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxRelay {

    private final OutboxMapper outboxMapper;
    private final RabbitTemplate rabbitTemplate;

    private static final int BATCH_SIZE = 100;

    @Scheduled(fixedRate = 1000)
    public void relay() {
        List<Outbox> pending = outboxMapper.selectPending(BATCH_SIZE);
        if (pending.isEmpty()) {
            return;
        }
        for (Outbox o : pending) {
            try {
                rabbitTemplate.convertAndSend(
                        FeedMqConfig.EXCHANGE, FeedMqConfig.ROUTING_KEY,
                        new PostCreatedEvent(o.getPostId(), o.getUserId()));
                outboxMapper.markSent(o.getId());
            } catch (Exception e) {
                log.warn("发送outbox消息失败: id={}, postId={}, 稍后重试", o.getId(), o.getPostId(), e);
            }
        }
    }
}
