package org.example.simpleweibobackend.common.exception;

import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.PostVO;
import org.example.simpleweibobackend.post.create.CreatePostController;
import org.example.simpleweibobackend.post.create.CreatePostRequest;
import org.example.simpleweibobackend.post.create.CreatePostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 全局异常处理器行为验证:校验失败、缺请求头、坏 JSON、业务异常、错误方法各自返回正确状态码
 */
@WebMvcTest(CreatePostController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreatePostService createPostService;

    @Test
    void validPostReturnsSuccess() throws Exception {
        when(createPostService.createPost(any(CreatePostRequest.class), any()))
                .thenReturn(PostVO.from(Post.builder().userId(1L).title("标题").content("内容").build()));

        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"标题\",\"content\":\"内容\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("标题"));
    }

    @Test
    void blankTitleReturns400WithFieldMessage() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"content\":\"内容\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("帖子标题不能为空"));
    }

    @Test
    void missingUserHeaderReturns400() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"标题\",\"content\":\"内容\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void malformedJsonReturns400() throws Exception {
        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("not-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void auditRejectedReturns400WithBusinessCode1001() throws Exception {
        when(createPostService.createPost(any(CreatePostRequest.class), any()))
                .thenThrow(new BizException(ErrorCode.AUDIT_REJECTED, "内容存在风险，发帖被拒绝"));

        mockMvc.perform(post("/api/posts")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"标题\",\"content\":\"含敏感词\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(1001))
                .andExpect(jsonPath("$.message").value("内容存在风险，发帖被拒绝"));
    }

    @Test
    void getOnPostOnlyRouteReturns405() throws Exception {
        mockMvc.perform(get("/api/posts"))
                .andExpect(status().isMethodNotAllowed())
                .andExpect(jsonPath("$.code").value(405));
    }
}
