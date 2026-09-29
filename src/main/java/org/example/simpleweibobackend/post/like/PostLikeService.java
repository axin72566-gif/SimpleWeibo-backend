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

/** 点赞服务 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostLikeService {

    /** 点赞脚本:SADD 成功则 INCR */
    private static final RedisScript<Long> LIKE_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('SADD', KEYS[1], ARGV[1]) == 1 then
                return redis.call('INCR', KEYS[2])
            end
            return nil
            """, Long.class);

    /** 回滚脚本:SREM 成功才 DECR */
    private static final RedisScript<Long> UNLIKE_ROLLBACK_SCRIPT = new DefaultRedisScript<>("""
            if redis.call('SREM', KEYS[1], ARGV[1]) == 1 then
                return redis.call('DECR', KEYS[2])
            end
            return nil
            """, Long.class);

    private final PostMapper postMapper;
    private final PostLikePublisher postLikePublisher;
    private final StringRedisTemplate stringRedisTemplate;

    /** 点赞,返回最新点赞数 */
    public Long like(Long userId, Long postId) {
        if (postMapper.selectById(postId) == null) {
            log.info("点赞失败, 帖子不存在: userId={}, postId={}", userId, postId);
            throw new BizException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }

        Long count;
        try {
            count = stringRedisTemplate.execute(LIKE_SCRIPT,
                    List.of(PostLikeRedisKey.POST_LIKE_USERS + postId, PostLikeRedisKey.POST_LIKE_COUNT + postId),
                    String.valueOf(userId));
        } catch (Exception e) {
            log.error("点赞失败, Redis执行Lua脚本异常: userId={}, postId={}", userId, postId, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR, "点赞失败");
        }
        if (count == null) {
            log.info("重复点赞: userId={}, postId={}", userId, postId);
            throw new BizException(ErrorCode.REPEAT_LIKE, "请勿重复点赞");
        }

        try {
            postLikePublisher.publish(new PostLikeEvent(postId, userId));
        } catch (Exception e) {
            log.error("点赞事件发送失败,回滚 Redis: userId={}, postId={}", userId, postId, e);
            try {
                stringRedisTemplate.execute(UNLIKE_ROLLBACK_SCRIPT,
                        List.of(PostLikeRedisKey.POST_LIKE_USERS + postId, PostLikeRedisKey.POST_LIKE_COUNT + postId),
                        String.valueOf(userId));
            } catch (Exception re) {
                log.error("Redis 回滚失败,计数存在漂移,待对账修复: userId={}, postId={}", userId, postId, re);
            }
            throw new BizException(ErrorCode.INTERNAL_ERROR, "点赞失败");
        }
        return count;
    }
}
