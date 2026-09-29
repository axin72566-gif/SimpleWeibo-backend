package org.example.simpleweibobackend.post.like.kafka;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.PostLike;
import org.example.simpleweibobackend.post.like.PostLikeMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 点赞事件消费者 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeConsumer {

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;

    /** 批量落库 */
    @KafkaListener(topics = PostLikeTopic.TOPIC, groupId = "post-likes-group", batch = "true")
    @Transactional(rollbackFor = Exception.class)
    public void onLikeEvent(List<ConsumerRecord<String, String>> records) {
        Map<Long, List<PostLike>> likesByPost = new HashMap<>();
        for (ConsumerRecord<String, String> record : records) {
            PostLikeEvent event = JSONUtil.toBean(record.value(), PostLikeEvent.class);
            likesByPost.computeIfAbsent(event.getPostId(), k -> new ArrayList<>())
                    .add(PostLike.builder().postId(event.getPostId()).userId(event.getUserId()).build());
        }
        // 重复事件被唯一索引拦下,不会重复计数
        for (Map.Entry<Long, List<PostLike>> entry : likesByPost.entrySet()) {
            int inserted = postLikeMapper.insertBatch(entry.getValue());
            if (inserted > 0) {
                postMapper.update(null, Wrappers.<Post>update()
                        .setSql("like_count = like_count + " + inserted)
                        .eq("id", entry.getKey()));
            }
        }
        log.info("点赞事件批量落库完成,本批 {} 条", records.size());
    }
}
