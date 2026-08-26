package org.example.simpleweibobackend.post.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.post.entity.Inbox;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.InboxMapper;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.service.FeedService;
import org.example.simpleweibobackend.post.vo.FeedVO;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 50;

    private final InboxMapper inboxMapper;
    private final PostMapper postMapper;

    @Override
    public FeedVO getFeed(Long cursor, Integer size) {
        Long userId = UserContext.getUserId();
        int limit = size == null || size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
        List<Inbox> inboxItems = inboxMapper.selectList(new LambdaQueryWrapper<Inbox>()
                .eq(Inbox::getReceiverId, userId)
                .lt(cursor != null, Inbox::getId, cursor)
                .orderByDesc(Inbox::getId)
                .last("LIMIT " + limit));
        if (inboxItems.isEmpty()) {
            return new FeedVO(List.of(), null);
        }

        List<Long> postIds = inboxItems.stream().map(Inbox::getPostId).toList();
        Map<Long, Post> postMap = postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, Function.identity()));
        List<PostVO> items = postIds.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(PostVO::from)
                .toList();

        Long nextCursor = inboxItems.size() == limit
                ? inboxItems.getLast().getId()
                : null;
        return new FeedVO(items, nextCursor);
    }
}
