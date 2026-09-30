package org.example.simpleweibobackend.post.query;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostQueryService {

    private static final int MAX_PAGE_SIZE = 100;

    private final PostMapper postMapper;
    private final UserMapper userMapper;

    /** 根据 id 查询帖子详情,帖子不存在时抛 POST_NOT_FOUND */
    public PostVO getPostDetail(Long postId) {
        Post post = postMapper.selectById(postId);
        if (post == null) {
            log.info("查询帖子详情失败, 帖子不存在: postId={}", postId);
            throw new BizException(ErrorCode.POST_NOT_FOUND);
        }
        return PostVO.from(post, userMapper.selectById(post.getUserId()));
    }

    /** 分页查询帖子,按创建时间倒序,页码从 1 开始,单页条数上限 100 */
    public PageVO<PostVO> pagePosts(int page, int size) {
        page = Math.max(page, 1);
        size = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Page<Post> result = postMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Post>().orderByDesc(Post::getCreateTime, Post::getId));
        List<Post> posts = result.getRecords();
        List<Long> authorIds = posts.stream().map(Post::getUserId).distinct().toList();
        Map<Long, User> authors = authorIds.isEmpty() ? Map.of()
                : userMapper.selectByIds(authorIds).stream()
                        .collect(Collectors.toMap(User::getId, u -> u));
        List<PostVO> records = posts.stream()
                .map(post -> PostVO.from(post, authors.get(post.getUserId())))
                .toList();
        return PageVO.of(records, result.getTotal(), page, size);
    }
}
