package org.example.simpleweibobackend.post.hot.calculation;

import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.hot.ScoredPost;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 第一版热度模型：浏览量 * 0.5。
 */
@Component
public class DefaultHotPostCalculator implements HotPostCalculator {

    private static final double VIEW_COUNT_WEIGHT = 0.5;

    @Override
    public List<ScoredPost> calculate(List<Post> recallPosts) {
        List<ScoredPost> scoredPosts = new ArrayList<>(recallPosts.size());

        for (Post post : recallPosts) {
            double score = calculateScore(post);
            scoredPosts.add(new ScoredPost(post, score));
        }

        return scoredPosts;
    }

    private double calculateScore(Post post) {
        long viewCount = post.getViewCount() == null ? 0L : post.getViewCount();
        return viewCount * VIEW_COUNT_WEIGHT;
    }
}
