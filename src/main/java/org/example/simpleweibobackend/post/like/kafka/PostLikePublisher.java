package org.example.simpleweibobackend.post.like.kafka;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

/** 点赞事件发布者:事件 JSON 序列化后发往 post-likes,以 postId 为 key 保证同帖有序 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikePublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(PostLikeEvent event) {
        kafkaTemplate.send("post-likes", String.valueOf(event.getPostId()), JSONUtil.toJsonStr(event))
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("点赞事件发送失败,Redis 计数已加但可能未落库: postId={}, userId={}",
                                event.getPostId(), event.getUserId(), ex);
                    }
                });
    }
}
