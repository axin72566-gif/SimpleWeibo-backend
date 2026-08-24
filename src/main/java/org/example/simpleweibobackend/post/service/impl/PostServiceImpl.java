package org.example.simpleweibobackend.post.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.post.dto.CreatePostRequest;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.entity.Inbox;
import org.example.simpleweibobackend.post.mapper.InboxMapper;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.service.PostService;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.vo.PostDetailVO;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.FollowMapper;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.common.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FollowMapper followMapper;
    private final InboxMapper inboxMapper;

    @Override
    @Transactional
    public PostVO createPost(CreatePostRequest request) {
        Long userId = UserContext.getUserId();
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        postMapper.insert(post);

        Long postId = post.getId();
        List<Long> receiverIds = new ArrayList<>(followMapper.selectFanIds(userId));
        List<Inbox> items = receiverIds.stream().map(receiverId -> {
            Inbox item = new Inbox();
            item.setReceiverId(receiverId);
            item.setPostId(postId);
            item.setAuthorId(userId);
            return item;
        }).toList();
        inboxMapper.batchInsert(items);

        return PostVO.from(post);
    }

    @Override
    public PostDetailVO getPostDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "帖子不存在");
        }
        User author = userMapper.selectById(post.getUserId());
        return PostDetailVO.from(post, author);
    }
}
