package org.example.simpleweibobackend.post.hot.recall;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 按请求中的时间范围召回微博。
 */
@Component
@RequiredArgsConstructor
public class DefaultHotPostRecaller implements HotPostRecaller {

    private final PostMapper postMapper;

    @Override
    public List<Post> recall(RecallRequest request) {
        return postMapper.selectPostsCreatedBetween(
                request.getStartTime(), request.getEndTime());
    }
}
