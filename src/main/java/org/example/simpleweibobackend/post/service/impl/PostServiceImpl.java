package org.example.simpleweibobackend.post.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.UserContext;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.event.PostLikeEvent;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.queue.PostLikeEventQueue;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private static final String LIKE_USERS = "post:like:users:";
    private static final String LIKE_COUNT = "post:like:count:";

    private static final DefaultRedisScript<Long> LIKE_SCRIPT = new DefaultRedisScript<>("""
            local added = redis.call('SADD', KEYS[1], ARGV[1])
            if added == 0 then
                return -1
            end
            if redis.call('EXISTS', KEYS[2]) == 0 then
                redis.call('SET', KEYS[2], ARGV[2])
            end
            return redis.call('INCR', KEYS[2])
            """, Long.class);

    private final PostMapper postMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final PostLikeEventQueue postLikeEventQueue;

    @Override
    public PostVO createPost(CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        // 插入数据库
        postMapper.insert(post);
        return PostVO.from(post);
    }

    @Override
    public PostVO getPostById(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        return PostVO.from(post);
    }

    @Override
    public void likePost(Long postId) {
        Long userId = UserContext.getUserId();
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        String usersKey = LIKE_USERS + postId;
        String countKey = LIKE_COUNT + postId;
        Long likeCount = stringRedisTemplate.execute(
                LIKE_SCRIPT,
                List.of(usersKey, countKey),
                userId.toString(),
                post.getLikeCount().toString());
        if (likeCount == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "点赞失败");
        }
        if (likeCount == -1) {
            throw new BizException(ErrorCode.CONFLICT, "已点赞");
        }

        if (!postLikeEventQueue.offer(new PostLikeEvent(postId, userId))) {
            log.error("点赞事件入队失败, postId={}, userId={}", postId, userId);
        }
    }
}
