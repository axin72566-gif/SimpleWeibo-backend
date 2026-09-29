package org.example.simpleweibobackend.user.follow;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    /** 关注 */
    @PostMapping("/follows/{followedUserId}")
    public Result<Void> follow(@PathVariable Long followedUserId) {
        followService.follow(UserContext.getUserId(), followedUserId);
        return Result.success(null);
    }
}
