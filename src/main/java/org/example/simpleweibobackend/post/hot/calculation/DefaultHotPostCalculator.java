package org.example.simpleweibobackend.post.hot.calculation;

import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.hot.ScoredPost;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 热度模型：浏览量 / (发布天数 + 1)。
 */
@Component
public class DefaultHotPostCalculator implements HotPostCalculator {

    /**
     * 一天的秒数,用于把帖子年龄折算成天数
     */
    private static final double SECONDS_PER_DAY = 24 * 60 * 60.0;

    /**
     * 对候选帖子逐条计算热度分
     *
     * @param recallPosts 召回层输出的候选帖子
     * @return 携带热度分的帖子列表,顺序与输入一致
     */
    @Override
    public List<ScoredPost> calculate(List<Post> recallPosts) {
        List<ScoredPost> scoredPosts = new ArrayList<>(recallPosts.size());
        LocalDateTime calculatedAt = LocalDateTime.now();

        for (Post post : recallPosts) {
            double score = calculateScore(post, calculatedAt);
            scoredPosts.add(new ScoredPost(post, score));
        }

        return scoredPosts;
    }

    private double calculateScore(Post post, LocalDateTime calculatedAt) {
        long viewCount = post.getViewCount() == null ? 0 : post.getViewCount();

        long ageSeconds = Duration.between(post.getCreateTime(), calculatedAt).toSeconds();
        double ageDays = ageSeconds / SECONDS_PER_DAY;

        return viewCount / (ageDays + 1.0);
    }
}
