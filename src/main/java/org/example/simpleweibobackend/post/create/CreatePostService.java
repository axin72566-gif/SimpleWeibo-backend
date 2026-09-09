package org.example.simpleweibobackend.post.create;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostMapper;
import org.example.simpleweibobackend.post.PostVO;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreatePostService {

    private final PostMapper postMapper;

    public PostVO createPost(CreatePostRequest request, Long userId) {
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postMapper.insert(post);
        return PostVO.from(post);
    }
}
