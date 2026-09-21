package org.example.simpleweibobackend.post.query;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 帖子查询接口
 */
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostQueryController {

    private final PostQueryService postQueryService;

    /**
     * 按帖子 ID 查询详情
     *
     * @param id 帖子 ID
     * @return 帖子详情
     */
    @GetMapping("/{id}")
    public Result<PostVO> getPostById(@PathVariable Long id) {
        return Result.success(postQueryService.getPostById(id));
    }
}
