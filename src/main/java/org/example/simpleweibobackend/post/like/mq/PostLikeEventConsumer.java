package org.example.simpleweibobackend.post.like.mq;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.PostLike;
import org.example.simpleweibobackend.post.like.PostLikeMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostLikeEventConsumer {

    private static final int BATCH_SIZE = 500;

    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;

    @KafkaListener(topics = "like-topic", groupId = "post-like-persistence", batch = "true")
    @Transactional
    public void consume(List<String> messages) {
        List<PostLikeEvent> events = messages.stream()
                .map(message -> JSONUtil.toBean(message, PostLikeEvent.class))
                .toList();

        persist(events);
    }

    private void persist(List<PostLikeEvent> events) {
        List<PostLike> postLikes = events.stream()
                .map(event -> {
                    PostLike postLike = new PostLike();
                    postLike.setPostId(event.getPostId());
                    postLike.setUserId(event.getUserId());
                    return postLike;
                })
                .toList();
        postLikeMapper.insert(postLikes, BATCH_SIZE);

        Map<Long, Long> increments = events.stream()
                .collect(Collectors.groupingBy(PostLikeEvent::getPostId, Collectors.counting()));
        for (Map.Entry<Long, Long> entry : increments.entrySet()) {
            postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, entry.getKey())
                    .setIncrBy(Post::getLikeCount, entry.getValue()));
        }
    }
}
