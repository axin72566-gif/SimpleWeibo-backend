package org.example.simpleweibobackend.feed.controller;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.feed.service.FeedService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @GetMapping
    public Result<PageVO<PostVO>> getFeed(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        return Result.success(feedService.getFeed(page, size));
    }
}
