package org.example.simpleweibobackend.post.like.kafka;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** 点赞事件发布者:事件 JSON 序列化后发往 post-likes,以 postId 为 key 保证同帖有序;
 *  同步等待 broker 确认,失败抛异常,由调用方回滚 Redis,保证 like() 返回成功即两边都已落定 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikePublisher {

    /** 等待 broker 确认的超时;超时按失败处理,触发回滚 */
    private static final long SEND_TIMEOUT_SECONDS = 3;

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publish(PostLikeEvent event) {
        try {
            kafkaTemplate.send(PostLikeTopic.TOPIC, String.valueOf(event.getPostId()), JSONUtil.toJsonStr(event))
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("点赞事件发送被中断", e);
        } catch (Exception e) {
            throw new RuntimeException("点赞事件发送失败", e);
        }
    }
}
