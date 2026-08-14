package org.example.simpleweibobackend.post.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.outbox.entity.Outbox;
import org.example.simpleweibobackend.outbox.mapper.OutboxMapper;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.exception.BizException;
import org.example.simpleweibobackend.post.vo.PostDetailVO;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final OutboxMapper outboxMapper;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public PostVO createPost(CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postMapper.insert(post);

        Outbox outbox = new Outbox();
        outbox.setPostId(post.getId());
        outbox.setUserId(userId);
        outbox.setStatus("PENDING");
        outboxMapper.insert(outbox);

        return PostVO.from(post);
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        User author = userMapper.selectById(post.getUserId());
        return PostDetailVO.from(post, author);
    }

    @Override
    public PageVO<PostDetailVO> listPosts(int page, int size) {
        Page<Post> postPage = postMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<Post>().orderByDesc("create_time"));
        if (postPage.getRecords().isEmpty()) {
            return PageVO.of(List.of(), postPage.getTotal(), page, size);
        }
        List<Long> userIds = postPage.getRecords().stream()
                .map(Post::getUserId)
                .distinct()
                .toList();
        Map<Long, User> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        List<PostDetailVO> records = postPage.getRecords().stream()
                .map(post -> PostDetailVO.from(post, userMap.get(post.getUserId())))
                .toList();
        return PageVO.of(records, postPage.getTotal(), page, size);
    }
}
