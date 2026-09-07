package org.example.simpleweibobackend.post.create;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CreatePostController {

    private final CreatePostService createPostService;

    @PostMapping
    public Result<PostVO> createPost(@Valid @RequestBody CreatePostRequest request) {
        return Result.success(createPostService.createPost(request));
    }
}
