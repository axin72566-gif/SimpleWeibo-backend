package org.example.simpleweibobackend.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostDetailVO;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "帖子管理")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "发布帖子")
    @PostMapping
    public Result<PostVO> createPost(@Valid @RequestBody CreatePostRequest request) {
        return Result.success(postService.createPost(request));
    }

    @Operation(summary = "查看帖子详情")
    @GetMapping("/{id}")
    public Result<PostDetailVO> getPostDetail(@Parameter(description = "帖子ID") @PathVariable Long id) {
        return Result.success(postService.getPostDetail(id));
    }

    @Operation(summary = "首页帖子分页")
    @GetMapping
    public Result<PageVO<PostDetailVO>> listPosts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        return Result.success(postService.listPosts(page, size));
    }
}
