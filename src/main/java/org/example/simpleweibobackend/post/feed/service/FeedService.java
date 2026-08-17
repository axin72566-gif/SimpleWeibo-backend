package org.example.simpleweibobackend.post.feed.service;

import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.post.vo.PostVO;

public interface FeedService {

    PageVO<PostVO> getFeed(int page, int size);
}
