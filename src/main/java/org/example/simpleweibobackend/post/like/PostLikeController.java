package org.example.simpleweibobackend.post.like;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService likeService;

    /** 点赞 */
    @PostMapping("/likes/{postId}")
    public Result<Long> like(@PathVariable Long postId) {
        return Result.success(likeService.like(UserContext.getUserId(), postId));
    }
}
