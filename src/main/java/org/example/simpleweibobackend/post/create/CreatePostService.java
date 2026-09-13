package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.audit.AuditContext;
import org.example.simpleweibobackend.post.audit.PostAuditChain;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostService {

    private final PostMapper postMapper;

    private final PostAuditChain postAuditChain;

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
