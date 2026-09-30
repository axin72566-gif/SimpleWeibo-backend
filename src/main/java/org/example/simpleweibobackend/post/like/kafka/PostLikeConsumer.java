package org.example.simpleweibobackend.post.like.kafka;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 点赞事件消费者 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeConsumer {

    private final PostMapper postMapper;
    private final PostLikeMapper postLikeMapper;

    /**
     * 批量落库,同一帖子的消息同 key 同分区,批内保持顺序处理
     */
    @KafkaListener(topics = PostLikeTopic.TOPIC, groupId = "post-likes-group", batch = "true")
    @Transactional(rollbackFor = Exception.class)
    public void onLikeEvent(List<ConsumerRecord<String, String>> records) {
        Map<Long, List<PostLikeEvent>> eventsByPost = new LinkedHashMap<>();
        for (ConsumerRecord<String, String> record : records) {
            PostLikeEvent event = JSONUtil.toBean(record.value(), PostLikeEvent.class);
            eventsByPost.computeIfAbsent(event.getPostId(), k -> new ArrayList<>()).add(event);
        }

        for (Map.Entry<Long, List<PostLikeEvent>> entry : eventsByPost.entrySet()) {
            Long postId = entry.getKey();
            List<PostLike> pendingLikes = new ArrayList<>();
            for (PostLikeEvent event : entry.getValue()) {
                if (event.getType() == PostLikeEventType.UNLIKE) {
                    // 先落之前的点赞,保证同一用户 赞->取消->再赞 的顺序语义
                    if (!pendingLikes.isEmpty()) {
                        int inserted = postLikeMapper.insertBatch(pendingLikes);
                        if (inserted > 0) {
                            postMapper.update(null, Wrappers.<Post>update()
                                    .setSql("like_count = like_count + " + inserted)
                                    .eq("id", postId));
                        }
                        pendingLikes.clear();
                    }
                    int deleted = postLikeMapper.delete(new LambdaQueryWrapper<PostLike>()
                            .eq(PostLike::getUserId, event.getUserId())
                            .eq(PostLike::getPostId, postId));
                    if (deleted > 0) {
                        postMapper.update(null, Wrappers.<Post>update()
                                .setSql("like_count = like_count - 1")
                                .eq("id", postId));
                    }
                } else {
                    pendingLikes.add(PostLike.builder().postId(postId).userId(event.getUserId()).build());
                }
            }
            if (!pendingLikes.isEmpty()) {
                // 重复事件被唯一索引拦下,不会重复计数
                int inserted = postLikeMapper.insertBatch(pendingLikes);
                if (inserted > 0) {
                    postMapper.update(null, Wrappers.<Post>update()
                            .setSql("like_count = like_count + " + inserted)
                            .eq("id", postId));
                }
            }
        }
        log.info("点赞事件批量落库完成,本批 {} 条", records.size());
    }
}
