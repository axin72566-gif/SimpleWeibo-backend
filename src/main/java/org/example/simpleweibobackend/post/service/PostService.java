package org.example.simpleweibobackend.post.service;

import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.vo.PostVO;

public interface PostService {

    PostVO createPost(CreatePostRequest request);

    PostVO getPostById(Long id);
}
