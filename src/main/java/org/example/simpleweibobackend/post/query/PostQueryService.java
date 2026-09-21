package org.example.simpleweibobackend.post.query;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.stereotype.Service;

/**
 * 帖子查询服务
 */
@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostMapper postMapper;

    /**
     * 查询帖子详情
     *
     * @param id 帖子 ID
     * @return 帖子详情
     * @throws BizException 帖子不存在(404)
     */
    public PostVO getPostById(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        return PostVO.from(post);
    }
}
