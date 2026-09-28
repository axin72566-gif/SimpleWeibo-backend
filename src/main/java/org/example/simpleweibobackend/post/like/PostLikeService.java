package org.example.simpleweibobackend.post.like;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.kafka.PostLikeEvent;
import org.example.simpleweibobackend.post.like.kafka.PostLikePublisher;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

/** 点赞服务:Lua 原子 SADD + INCR,新增点赞发 Kafka 事件异步落库 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    private static final String POST_LIKE_USERS = "post:like:users:";
    private static final String POST_LIKE_COUNT = "post:like:count:";

    /** SADD 成功则 INCR 并返回最新点赞数;已点赞返回 nil 表示失败 */
    private static final RedisScript<Long> LIKE_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('SADD', KEYS[1], ARGV[1]) == 1 then
                return redis.call('INCR', KEYS[2])
            end
            return nil
            """, Long.class);

    private final PostMapper postMapper;
    private final PostLikePublisher postLikePublisher;
    private final StringRedisTemplate stringRedisTemplate;

    /** 点赞;成功返回最新点赞数,重复点赞返回失败 */
    public Long like(Long userId, Long postId) {
        if (postMapper.selectById(postId) == null) {
            log.info("点赞失败, 帖子不存在: userId={}, postId={}", userId, postId);
            throw new BizException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }

        Long count;
        try {
            count = stringRedisTemplate.execute(LIKE_SCRIPT,
                    List.of(POST_LIKE_USERS + postId, POST_LIKE_COUNT + postId),
                    String.valueOf(userId));
        } catch (Exception e) {
            log.error("点赞失败, Redis执行Lua脚本异常: userId={}, postId={}", userId, postId, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "点赞失败");
        }
        if (count == null) {
            log.info("重复点赞: userId={}, postId={}", userId, postId);
            throw new BizException(ErrorCode.REPEAT_LIKE, "请勿重复点赞");
        }

        postLikePublisher.publish(new PostLikeEvent(postId, userId));
        return count;
    }
}
