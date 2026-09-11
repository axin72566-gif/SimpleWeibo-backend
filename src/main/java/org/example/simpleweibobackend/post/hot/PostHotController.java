package org.example.simpleweibobackend.post.hot;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostHotController {

    private final PostHotService postHotService;

    @GetMapping("/hot")
    public Result<List<PostVO>> getHotPosts() {
        return Result.success(postHotService.getHotPosts());
    }
}
