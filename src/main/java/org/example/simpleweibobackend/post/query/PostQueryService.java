package org.example.simpleweibobackend.post.query;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostMapper postMapper;

    public PostVO getPostById(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        return PostVO.from(post);
    }
}
