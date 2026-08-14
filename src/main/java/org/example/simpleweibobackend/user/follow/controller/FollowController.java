package org.example.simpleweibobackend.user.follow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.user.follow.service.FollowService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "关注管理")
@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @Operation(summary = "关注用户")
    @PostMapping("/{userId}")
    public Result<Void> follow(@Parameter(description = "目标用户ID") @PathVariable Long userId) {
        followService.follow(userId);
        return Result.success();
    }

    @Operation(summary = "取消关注")
    @DeleteMapping("/{userId}")
    public Result<Void> unfollow(@Parameter(description = "目标用户ID") @PathVariable Long userId) {
        followService.unfollow(userId);
        return Result.success();
    }
}
