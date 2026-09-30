package org.example.simpleweibobackend.post.feed;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    /** 关注流, 按发帖时间倒序 */
    @GetMapping
    public Result<List<PostVO>> getFeed() {
        return Result.success(feedService.getFeed(UserContext.getUserId()));
    }
}
