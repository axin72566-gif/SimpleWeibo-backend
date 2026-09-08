package org.example.simpleweibobackend.post.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.UserContext;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.event.PostLikeEvent;
import org.example.simpleweibobackend.post.like.event.PostLikeEventQueue;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {

    private static final String LIKE_USERS = "post:like:users:";

    private final PostMapper postMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostLikeEventQueue postLikeEventQueue;

    public void likePost(Long postId) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        String usersKey = LIKE_USERS + postId;
        Long added = stringRedisTemplate.opsForSet().add(usersKey, userId.toString());
        if (added == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "点赞失败");
        }
        if (added == 0) {
            throw new BizException(ErrorCode.CONFLICT, "已点赞");
        }

        if (!postLikeEventQueue.offer(new PostLikeEvent(postId, userId))) {
            log.error("点赞事件入队失败, postId={}, userId={}", postId, userId);
        }
    }
}
