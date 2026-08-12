package org.example.simpleweibobackend.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.feed.config.FeedMqConfig;
import org.example.simpleweibobackend.feed.dto.PostCreatedEvent;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final RabbitTemplate rabbitTemplate;

    @Override
    public PostVO createPost(CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(request.getContent());
        postMapper.insert(post);
        rabbitTemplate.convertAndSend(
                FeedMqConfig.EXCHANGE, FeedMqConfig.ROUTING_KEY,
                new PostCreatedEvent(post.getId(), post.getUserId()));
        return PostVO.from(post);
    }
}
