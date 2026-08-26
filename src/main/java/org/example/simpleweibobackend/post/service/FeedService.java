package org.example.simpleweibobackend.post.service;

import org.example.simpleweibobackend.post.vo.FeedVO;

public interface FeedService {

    FeedVO getFeed(Long cursor, Integer size);
}
