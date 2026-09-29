package org.example.simpleweibobackend.post.query;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostQueryController {

    private final PostQueryService postQueryService;

    /** 根据 id 查询帖子详情 */
    @GetMapping("/{postId}")
    public Result<PostVO> getPostDetail(@PathVariable Long postId) {
        return Result.success(postQueryService.getPostDetail(postId));
    }
}
