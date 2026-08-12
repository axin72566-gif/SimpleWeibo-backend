package org.example.simpleweibobackend.follow.controller;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.follow.service.FollowService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{userId}")
    public Result<Void> follow(@PathVariable Long userId) {
        followService.follow(userId);
        return Result.success();
    }

    @DeleteMapping("/{userId}")
    public Result<Void> unfollow(@PathVariable Long userId) {
        followService.unfollow(userId);
        return Result.success();
    }
}
