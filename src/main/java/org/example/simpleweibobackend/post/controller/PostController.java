package org.example.simpleweibobackend.post.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostDetailVO;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping
    public Result<PostVO> createPost(@Valid @RequestBody CreatePostRequest request) {
        return Result.success(postService.createPost(request));
    }

    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(@PathVariable Long id) {
        return Result.success(postService.getPostDetail(id));
    }
}
