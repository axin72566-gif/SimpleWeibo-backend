package org.example.simpleweibobackend.post.queue.consumer;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.entity.PostLike;
import org.example.simpleweibobackend.post.event.PostLikeEvent;
import org.example.simpleweibobackend.post.mapper.PostLikeMapper;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.queue.PostLikeEventQueue;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostLikeEventConsumer {

    private static final int BATCH_SIZE = 100;

    private final PostLikeEventQueue postLikeEventQueue;
    private final PostLikeMapper postLikeMapper;
    private final PostMapper postMapper;
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedDelay = 100)
    public void consume() {
        for (int i = 0; i < BATCH_SIZE; i++) {
            PostLikeEvent event = postLikeEventQueue.poll();
            if (event == null) {
                return;
            }

            try {
                transactionTemplate.executeWithoutResult(status -> persist(event));
            } catch (Exception e) {
                log.error("点赞事件处理失败, postId={}, userId={}",
                        event.getPostId(), event.getUserId(), e);
            }
        }
    }

    private void persist(PostLikeEvent event) {
        boolean alreadyPersisted = postLikeMapper.exists(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, event.getPostId())
                .eq(PostLike::getUserId, event.getUserId()));
        if (alreadyPersisted) {
            return;
        }

        PostLike postLike = new PostLike();
        postLike.setPostId(event.getPostId());
        postLike.setUserId(event.getUserId());
        postLikeMapper.insert(postLike);

        int updated = postMapper.update(null, new LambdaUpdateWrapper<Post>()
                .eq(Post::getId, event.getPostId())
                .setIncrBy(Post::getLikeCount, 1));
        if (updated != 1) {
            throw new IllegalStateException("更新帖子点赞数失败");
        }
    }
}
