package org.example.simpleweibobackend.post.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService {

    private final PostMapper postMapper;

    /** 根据 id 查询帖子详情,帖子不存在时抛 POST_NOT_FOUND */
    public PostVO getPostDetail(Long postId) {
        var post = postMapper.selectById(postId);
        if (post == null) {
            log.info("查询帖子详情失败, 帖子不存在: postId={}", postId);
            throw new BizException(ErrorCode.POST_NOT_FOUND);
        }
        return PostVO.from(post);
    }
}
