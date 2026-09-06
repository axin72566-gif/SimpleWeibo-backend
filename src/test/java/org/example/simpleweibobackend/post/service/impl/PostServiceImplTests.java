package org.example.simpleweibobackend.post.service.impl;

import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.post.vo.PostVO;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostServiceImplTests {

    private final PostMapper postMapper = mock(PostMapper.class);
    private final PostServiceImpl postService = new PostServiceImpl(postMapper);

    @Test
    void shouldReturnPostWhenPostExists() {
        LocalDateTime createTime = LocalDateTime.now();
        Post post = new Post();
        post.setId(1L);
        post.setUserId(2L);
        post.setTitle("测试标题");
        post.setContent("测试内容");
        post.setCreateTime(createTime);
        when(postMapper.selectById(1L)).thenReturn(post);

        PostVO result = postService.getPostById(1L);

        assertEquals(1L, result.getId());
        assertEquals(2L, result.getUserId());
        assertEquals("测试标题", result.getTitle());
        assertEquals("测试内容", result.getContent());
        assertEquals(createTime, result.getCreateTime());
    }

    @Test
    void shouldThrowNotFoundWhenPostDoesNotExist() {
        when(postMapper.selectById(99L)).thenReturn(null);

        BizException exception = assertThrows(BizException.class,
                () -> postService.getPostById(99L));

        assertEquals(404, exception.getCode());
        assertEquals("帖子不存在", exception.getMessage());
    }
}
