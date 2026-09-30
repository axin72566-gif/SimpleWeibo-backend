package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.PostAuditChain;
import org.example.simpleweibobackend.post.feed.kafka.FeedPushEvent;
import org.example.simpleweibobackend.post.feed.kafka.FeedPushPublisher;
import org.example.simpleweibobackend.user.UserMapper;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

/** 发帖服务 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePostService {

    private final PostMapper postMapper;

    private final UserMapper userMapper;

    private final PostAuditChain postAuditChain;

    private final FeedPushPublisher feedPushPublisher;

    public PostVO createPost(CreatePostRequest request, Long userId) {
        AuditContext auditContext = AuditContext.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        postAuditChain.audit(auditContext);

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        int insert = postMapper.insert(post);
        if (insert != 1) {
            log.error("发帖失败, 数据库操作失败: userId={}", userId);
            throw new BizException(ErrorCode.INTERNAL_ERROR);
        }

        // 落库成功后发事件, 消费端异步写扩散到粉丝收件箱
        long createTimeMillis = post.getCreateTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        feedPushPublisher.publish(FeedPushEvent.builder()
                .postId(post.getId())
                .publisherId(post.getUserId())
                .createTimeMillis(createTimeMillis)
                .build());
        return PostVO.from(post, userMapper.selectById(userId));
    }
}