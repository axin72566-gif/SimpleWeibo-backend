package org.example.simpleweibobackend.post.feed.kafka;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/** 帖子推送事件发布者 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FeedPushPublisher {

    /** 等待 broker 确认的超时 */
    private static final long SEND_TIMEOUT_SECONDS = 3;

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发帖落库成功后调用。事件丢失只影响该帖的收件箱推送,读路径可回源兜底,
     * 因此发送失败仅记日志,不抛出把已成功的发帖变成失败响应
     */
    public void publish(FeedPushEvent event) {
        try {
            kafkaTemplate.send(FeedPushTopic.TOPIC, String.valueOf(event.getPostId()), JSONUtil.toJsonStr(event))
                    .get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("推送事件发送被中断: postId={}", event.getPostId(), e);
        } catch (Exception e) {
            log.error("推送事件发送失败: postId={}", event.getPostId(), e);
        }
    }
}
