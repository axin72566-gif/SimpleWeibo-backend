package org.example.simpleweibobackend.post.hot;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.hot.calculation.HotPostCalculator;
import org.example.simpleweibobackend.post.hot.ranking.HotPostRanker;
import org.example.simpleweibobackend.post.hot.recall.HotPostRecaller;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 热榜服务:召回 → 计算热度 → 排序 的三段式流水线
 */
@Service
@RequiredArgsConstructor
public class PostHotService {

    private final HotPostRecaller hotPostRecaller;
    private final HotPostCalculator hotPostCalculator;
    private final HotPostRanker hotPostRanker;

    /**
     * 获取热榜:召回候选帖子,逐条计算热度分,排序截取榜单后转视图对象
     *
     * @return 热度从高到低的热榜帖子列表
     */
    public List<PostVO> getHotPosts() {
        List<Post> recallPosts = hotPostRecaller.recall();
        List<ScoredPost> scoredPosts = hotPostCalculator.calculate(recallPosts);
        List<Post> hotPosts = hotPostRanker.rank(scoredPosts);

        return hotPosts.stream()
                .map(PostVO::from)
                .toList();

    }
}
