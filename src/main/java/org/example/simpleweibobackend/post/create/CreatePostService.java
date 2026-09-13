package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.audit.AuditContext;
import org.example.simpleweibobackend.post.audit.PostAuditChain;
import org.springframework.stereotype.Service;

/**
 * 发帖服务:审核通过后才落库
 */
@Service
@RequiredArgsConstructor
public class CreatePostService {

    private final PostMapper postMapper;

    private final PostAuditChain postAuditChain;

    /**
     * 发布帖子:先构建审核上下文走责任链(拒绝则抛业务异常),
     * 通过后插入帖子并返回视图对象
     *
     * @param request 帖子内容(标题 + 正文)
     * @param userId  发布者用户 ID
     * @return 发布成功的帖子信息
     */
    public PostVO createPost(CreatePostRequest request, Long userId) {
        AuditContext auditContext = AuditContext.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        postAuditChain.audit(auditContext);

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .content(request.getContent())
                .build();
        postMapper.insert(post);
        return PostVO.from(post);
    }
}
