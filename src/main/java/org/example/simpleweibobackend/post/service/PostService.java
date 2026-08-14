package org.example.simpleweibobackend.post.service;

import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.vo.PostDetailVO;
import org.example.simpleweibobackend.post.vo.PostVO;

public interface PostService {

    PostVO createPost(CreatePostRequest request);

    PostDetailVO getPostDetail(Long id);
}
