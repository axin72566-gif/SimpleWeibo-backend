package org.example.simpleweibobackend.post.create;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CreatePostController {

    private final CreatePostService createPostService;

    /**
     * 发布新帖子,先经审核责任链再入库;userId 由网关通过 X-User-Id 头传入
     */
    @PostMapping
    public Result<PostVO> createPost(@Valid @RequestBody CreatePostRequest request,
                                     @RequestHeader("X-User-Id") Long userId) {
        return Result.success(createPostService.createPost(request, userId));
    }
}
