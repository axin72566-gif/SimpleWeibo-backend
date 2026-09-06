package org.example.simpleweibobackend.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    @Override
    public PostVO createPost(CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        // 插入数据库
        postMapper.insert(post);
        return PostVO.from(post);
    }

    @Override
    public PostVO getPostById(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        return PostVO.from(post);
    }
}
