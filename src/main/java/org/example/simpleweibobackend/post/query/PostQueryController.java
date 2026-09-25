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

    @GetMapping("/{id}")
    public Result<PostVO> getPostById(@PathVariable Long id) {
        return Result.success(postQueryService.getPostById(id));
    }
}
