package org.example.simpleweibobackend.post.create;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.ratelimit.RateLimit;
import org.example.simpleweibobackend.ratelimit.RateLimitAlgorithm;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class CreatePostController {

    private final CreatePostService createPostService;

    /** 发布新帖子 */
    @PostMapping
    @RateLimit(key = "post:create", limit = 5, window = 80,
            timeUnit = TimeUnit.SECONDS, algorithm = RateLimitAlgorithm.SLIDING_WINDOW)
    public Result<PostVO> createPost(@Valid @RequestBody CreatePostRequest request) {
        return Result.success(createPostService.createPost(request, UserContext.getUserId()));
    }
}
