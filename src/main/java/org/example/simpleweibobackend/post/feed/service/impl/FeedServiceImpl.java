package org.example.simpleweibobackend.post.feed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.post.feed.entity.Inbox;
import org.example.simpleweibobackend.post.feed.mapper.InboxMapper;
import org.example.simpleweibobackend.post.feed.service.FeedService;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final InboxMapper inboxMapper;
    private final PostMapper postMapper;

    @Override
    public PageVO<PostVO> getFeed(int page, int size) {
        Long userId = UserContext.getUserId();
        Page<Inbox> inboxPage = inboxMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<Inbox>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time"));
        if (inboxPage.getRecords().isEmpty()) {
            return PageVO.of(List.of(), inboxPage.getTotal(), page, size);
        }
        List<Long> postIds = inboxPage.getRecords().stream()
                .map(Inbox::getPostId)
                .toList();
        Map<Long, Post> postMap = postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, p -> p));
        List<PostVO> records = inboxPage.getRecords().stream()
                .map(Inbox::getPostId)
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(PostVO::from)
                .toList();
        return PageVO.of(records, inboxPage.getTotal(), page, size);
    }
}
