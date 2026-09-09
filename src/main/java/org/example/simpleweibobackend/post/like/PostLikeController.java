package org.example.simpleweibobackend.post.like;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PutMapping("/{postId}/like")
    public Result<Void> likePost(@PathVariable Long postId,
                                 @RequestHeader("X-User-Id") Long userId) {
        postLikeService.likePost(postId, userId);
        return Result.success();
    }
}
