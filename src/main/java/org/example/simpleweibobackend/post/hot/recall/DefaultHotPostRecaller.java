package org.example.simpleweibobackend.post.hot.recall;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 召回最近七天内发布的微博。
 */
@Component
@RequiredArgsConstructor
public class DefaultHotPostRecaller implements HotPostRecaller {

    /**
     * 召回时间窗口:最近 7 天发布的帖子
     */
    private static final int RECALL_DAYS = 7;

    /**
     * 召回条数上限,防止候选集过大
     */
    private static final int RECALL_LIMIT = 1000;

    private final PostMapper postMapper;

    /**
     * 召回最近 7 天内发布的帖子作为热榜候选
     */
    @Override
    public List<Post> recall() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(RECALL_DAYS);
        return postMapper.selectPreHostPosts(startTime, endTime, RECALL_LIMIT);
    }
}
