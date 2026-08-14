package org.example.simpleweibobackend.post.feed.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.post.feed.entity.FeedItem;
import org.example.simpleweibobackend.post.feed.mapper.FeedItemMapper;
import org.example.simpleweibobackend.post.feed.service.FeedService;
import org.example.simpleweibobackend.user.follow.mapper.FollowMapper;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedServiceImpl implements FeedService {

    private final FeedItemMapper feedItemMapper;
    private final FollowMapper followMapper;
    private final PostMapper postMapper;

    @Override
    public void fanout(Post post) {
        List<Long> followerIds = followMapper.selectFollowerIds(post.getUserId());
        List<FeedItem> items = new ArrayList<>(followerIds.size() + 1);
        items.add(buildItem(post.getUserId(), post));
        for (Long followerId : followerIds) {
            items.add(buildItem(followerId, post));
        }
        feedItemMapper.batchInsert(items);
    }

    @Override
    public PageVO<PostVO> getFeed(int page, int size) {
        Long userId = UserContext.getUserId();
        Page<FeedItem> feedPage = feedItemMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<FeedItem>()
                        .eq("user_id", userId)
                        .orderByDesc("create_time"));
        if (feedPage.getRecords().isEmpty()) {
            return PageVO.of(List.of(), feedPage.getTotal(), page, size);
        }
        List<Long> postIds = feedPage.getRecords().stream()
                .map(FeedItem::getPostId)
                .toList();
        Map<Long, Post> postMap = postMapper.selectBatchIds(postIds).stream()
                .collect(Collectors.toMap(Post::getId, p -> p));
        List<PostVO> records = feedPage.getRecords().stream()
                .map(FeedItem::getPostId)
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(PostVO::from)
                .toList();
        return PageVO.of(records, feedPage.getTotal(), page, size);
    }

    private FeedItem buildItem(Long userId, Post post) {
        FeedItem item = new FeedItem();
        item.setUserId(userId);
        item.setPostId(post.getId());
        item.setPostUserId(post.getUserId());
        return item;
    }
}
