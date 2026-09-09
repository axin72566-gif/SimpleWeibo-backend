package org.example.simpleweibobackend.post.like;

import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.like.mq.PostLikeEvent;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final PostMapper postMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public void likePost(Long postId, Long userId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }

        String usersKey = "post:like:users:" + postId;
        Long added = stringRedisTemplate.opsForSet().add(usersKey, userId.toString());
        if (added == 0) {
            throw new BizException(ErrorCode.CONFLICT, "已点赞");
        }

        String postLikeEvent = JSONUtil.toJsonStr(new PostLikeEvent(postId, userId));
        kafkaTemplate.send("like-topic", postId.toString(), postLikeEvent);
    }
}
