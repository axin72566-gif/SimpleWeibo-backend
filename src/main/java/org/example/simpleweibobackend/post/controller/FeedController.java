package org.example.simpleweibobackend.post.controller;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.service.FeedService;
import org.example.simpleweibobackend.post.vo.FeedVO;
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
    public Result<FeedVO> getFeed(@RequestParam(required = false) Long cursor,
                                  @RequestParam(required = false) Integer size) {
        return Result.success(feedService.getFeed(cursor, size));
    }
}
