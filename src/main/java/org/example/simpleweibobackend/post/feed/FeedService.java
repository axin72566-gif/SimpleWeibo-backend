package org.example.simpleweibobackend.post.feed;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.follow.Follow;
import org.example.simpleweibobackend.user.follow.FollowMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/** 关注流读路径: Redis只出帖子ID(收件箱+大V发件箱), 查内容和排序都交给MySQL */
@Slf4j
@Service
@RequiredArgsConstructor
public class FeedService {

    private final FollowMapper followMapper;

    private final PostMapper postMapper;

    private final UserMapper userMapper;

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 关注流, 按发帖时间倒序全量返回
     */
    public List<PostVO> getFeed(Long userId) {
        // 我关注的人, 没有关注就没有feed
        List<Long> followedIds = followMapper.selectList(Wrappers.<Follow>lambdaQuery()
                        .select(Follow::getFollowedId)
                        .eq(Follow::getFollowerId, userId))
                .stream().map(Follow::getFollowedId).toList();
        if (followedIds.isEmpty()) {
            return List.of();
        }

        // 帖子ID来源: 我的收件箱(普通博主推流) + 我关注的每个人的发件箱(只有大V的发件箱存在, 没有的key查出为空)
        List<String> sourceKeys = new ArrayList<>();
        sourceKeys.add(FeedRedisKey.FEED_INBOX + userId);
        followedIds.forEach(followedId -> sourceKeys.add(FeedRedisKey.FEED_OUTBOX + followedId));

        // 逐个源全量拉取帖子ID (收件箱和发件箱在写侧按粉丝量分流, 同一帖子只会落在一边, 无需去重)
        ZSetOperations<String, String> zSetOps = stringRedisTemplate.opsForZSet();
        List<Long> postIds = new ArrayList<>();
        for (String sourceKey : sourceKeys) {
            for (String postId : zSetOps.reverseRange(sourceKey, 0, -1)) {
                postIds.add(Long.parseLong(postId));
            }
        }
        if (postIds.isEmpty()) {
            return List.of();
        }

        // 回MySQL查帖子: 已删除的查不到, 已取关作者的丢弃(取关靠读时过滤), 按创建时间倒序(同秒的按ID倒序)
        Set<Long> followedIdSet = new HashSet<>(followedIds);
        List<Post> posts = postMapper.selectByIds(postIds).stream()
                .filter(post -> followedIdSet.contains(post.getUserId()))
                .sorted(Comparator.comparing(Post::getCreateTime, Comparator.reverseOrder())
                        .thenComparing(Post::getId, Comparator.reverseOrder()))
                .toList();
        if (posts.isEmpty()) {
            return List.of();
        }

        // 批量查作者并组装
        Map<Long, User> authorById = userMapper.selectByIds(
                posts.stream().map(Post::getUserId).toList()).stream()
                .collect(Collectors.toMap(User::getId, user -> user));
        return posts.stream()
                .map(post -> PostVO.from(post, authorById.get(post.getUserId())))
                .toList();
    }
}
