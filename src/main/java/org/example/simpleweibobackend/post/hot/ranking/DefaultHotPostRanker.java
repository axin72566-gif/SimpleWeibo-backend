package org.example.simpleweibobackend.post.hot.ranking;

import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.hot.ScoredPost;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * 按热度分数从高到低返回前十条微博。
 */
@Component
public class DefaultHotPostRanker implements HotPostRanker {

    /**
     * 热榜展示条数
     */
    private static final int HOT_POST_LIMIT = 10;

    /**
     * 按热度分从高到低排序并截取前十
     */
    @Override
    public List<Post> rank(List<ScoredPost> scoredPosts) {
        return scoredPosts.stream()
                .sorted(Comparator.comparingDouble(ScoredPost::getScore).reversed())
                .limit(HOT_POST_LIMIT)
                .map(ScoredPost::getPost)
                .toList();
    }
}
