package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.AuditResultEnum;
import org.example.simpleweibobackend.post.create.audit.PostAuditChain;
import org.springframework.stereotype.Service;

/**
 * 发帖服务:审核通过后才落库
 */
@Service
@RequiredArgsConstructor
public class CreatePostService {

    private final PostMapper postMapper;

    private final PostAuditChain postAuditChain;

    public PostVO createPost(CreatePostRequest request, Long userId) {
        AuditContext auditContext = AuditContext.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        postAuditChain.audit(auditContext);

        AuditResultEnum result = auditContext.getResult();
        switch (result) {
            case REJECT ->
                    throw new BizException(ErrorCode.AUDIT_REJECTED, String.join("；", auditContext.getReasons()));
            case PASS, MANUAL_REVIEW -> {
                Post post = Post.builder()
                        .userId(userId)
                        .title(request.getTitle())
                        .content(request.getContent())
                        .auditStatus(result.getCode())
                        .build();
                postMapper.insert(post);
                return PostVO.from(post);
            }
            default -> throw new IllegalArgumentException("未知的审核结果: " + result);
        }
    }
}