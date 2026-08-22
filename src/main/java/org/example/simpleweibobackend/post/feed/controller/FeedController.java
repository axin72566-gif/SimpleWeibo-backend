package org.example.simpleweibobackend.post.feed.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.feed.service.FeedService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Feed流")
@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @Operation(summary = "获取收件箱Feed流")
    @GetMapping
    public Result<PageVO<PostVO>> getFeed(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        return Result.success(feedService.getFeed(page, size));
    }
}
