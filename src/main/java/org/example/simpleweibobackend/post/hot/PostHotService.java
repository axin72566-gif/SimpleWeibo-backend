package org.example.simpleweibobackend.post.hot;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.hot.calculation.HotPostCalculator;
import org.example.simpleweibobackend.post.hot.ranking.HotPostRanker;
import org.example.simpleweibobackend.post.hot.recall.HotPostRecaller;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostHotService {

    private final HotPostRecaller hotPostRecaller;
    private final HotPostCalculator hotPostCalculator;
    private final HotPostRanker hotPostRanker;

    public List<PostVO> getHotPosts() {
        List<Post> recallPosts = hotPostRecaller.recall();

        List<ScoredPost> scoredPosts = hotPostCalculator.calculate(recallPosts);

        List<Post> hotPosts = hotPostRanker.rank(scoredPosts);

        return hotPosts.stream()
                .map(PostVO::from)
                .toList();

    }
}
