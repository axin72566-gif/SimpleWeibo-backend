package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.create.audit.AuditContext;
import org.example.simpleweibobackend.post.create.audit.PostAuditChain;
import org.springframework.stereotype.Service;

/**
 * 发帖服务:先过审核责任链,被拒则抛业务异常,走完即落库
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

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        int insert = postMapper.insert(post);
        if (insert != 1) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "发帖失败, 数据库操作失败");
        }
        return PostVO.from(post);
    }
}
