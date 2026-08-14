package org.example.simpleweibobackend.post.feed.service;

import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.vo.PostVO;

public interface FeedService {

    void fanout(Post post);

    PageVO<PostVO> getFeed(int page, int size);
}
