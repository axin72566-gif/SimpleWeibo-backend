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

    private static final int RECALL_DAYS = 7;
    private static final int RECALL_LIMIT = 1000;

    private final PostMapper postMapper;

    @Override
    public List<Post> recall() {
        LocalDateTime endTime = LocalDateTime.now();
        LocalDateTime startTime = endTime.minusDays(RECALL_DAYS);
        return postMapper.selectPreHostPosts(startTime, endTime, RECALL_LIMIT);
    }
}
