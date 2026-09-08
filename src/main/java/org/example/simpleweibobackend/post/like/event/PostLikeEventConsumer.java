package org.example.simpleweibobackend.post.like.event;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.PostLike;
import org.example.simpleweibobackend.post.like.PostLikeMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeEventConsumer {

    private static final int BATCH_SIZE = 500;

    private final PostLikeEventQueue postLikeEventQueue;
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedDelay = 100)
    public void consume() {
        List<PostLikeEvent> events = postLikeEventQueue.drain(BATCH_SIZE);
        if (events.isEmpty()) {
            return;
        }
        transactionTemplate.executeWithoutResult(status -> persist(events));
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
            int updated = postMapper.update(null, new LambdaUpdateWrapper<Post>()
                    .eq(Post::getId, entry.getKey())
                    .setIncrBy(Post::getLikeCount, entry.getValue()));
            if (updated != 1) {
                throw new IllegalStateException("更新帖子点赞数失败, postId=" + entry.getKey());
            }
        }
    }
}
