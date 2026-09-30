package org.example.simpleweibobackend.post.feed.kafka;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.simpleweibobackend.post.feed.FeedRedisKey;
import org.example.simpleweibobackend.user.follow.Follow;
import org.example.simpleweibobackend.user.follow.FollowMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/** 帖子推送事件消费者: 把帖子ID写进粉丝收件箱(写扩散) */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeedPushConsumer {

    /** 粉丝数超过该阈值视为大V, 跳过推送, 由读路径拉模式兜底 */
    private static final long BIG_V_FAN_THRESHOLD = 10_000L;

    private final FollowMapper followMapper;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 单条消费,ZADD 幂等,消费失败重试不会造成收件箱重复
     */
    @KafkaListener(topics = FeedPushTopic.TOPIC, groupId = "feed-push-group")
    public void onFeedPushEvent(ConsumerRecord<String, String> record) {
        FeedPushEvent event = JSONUtil.toBean(record.value(), FeedPushEvent.class);
        Long publisherId = event.getPublisherId();

        List<Long> followerIds = followMapper.selectList(Wrappers.<Follow>lambdaQuery()
                        .select(Follow::getFollowerId)
                        .eq(Follow::getFollowedId, publisherId))
                .stream().map(Follow::getFollowerId).toList();
        if (followerIds.size() > BIG_V_FAN_THRESHOLD) {
            stringRedisTemplate.opsForZSet().add(FeedRedisKey.FEED_OUTBOX + publisherId,
                    String.valueOf(event.getPostId()), event.getCreateTimeMillis());
            log.info("大V发帖写入发件箱: publisherId={}, postId={}, fanCount={}", publisherId, event.getPostId(), followerIds.size());
            return;
        }

        for (Long followerId : followerIds) {
            stringRedisTemplate.opsForZSet().add(FeedRedisKey.FEED_INBOX + followerId,
                    String.valueOf(event.getPostId()), event.getCreateTimeMillis());
        }
        log.info("帖子推送完成: publisherId={}, postId={}, fanCount={}", publisherId, event.getPostId(), followerIds.size());
    }
}
