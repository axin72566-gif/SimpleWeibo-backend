package org.example.simpleweibobackend.post.feed.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.feed.config.FeedMqConfig;
import org.example.simpleweibobackend.post.feed.dto.PostCreatedEvent;
import org.example.simpleweibobackend.post.feed.entity.Inbox;
import org.example.simpleweibobackend.post.feed.mapper.InboxMapper;
import org.example.simpleweibobackend.user.mapper.FollowMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeedFanoutConsumer {

    private final InboxMapper inboxMapper;
    private final FollowMapper followMapper;

    @RabbitListener(queues = FeedMqConfig.QUEUE)
    public void onPostCreated(PostCreatedEvent event) {
        log.info("收到发帖事件: postId={}, userId={}", event.getPostId(), event.getUserId());
        List<Long> receiverIds = new ArrayList<>(followMapper.selectFollowerIds(event.getUserId()));
        receiverIds.add(event.getUserId());
        List<Inbox> items = receiverIds.stream().map(receiverId -> {
            Inbox item = new Inbox();
            item.setUserId(receiverId);
            item.setPostId(event.getPostId());
            item.setPostUserId(event.getUserId());
            return item;
        }).toList();
        inboxMapper.batchInsert(items);
    }
}
