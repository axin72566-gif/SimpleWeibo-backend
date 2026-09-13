package org.example.simpleweibobackend.post.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发帖请求参数
 */
@Data
public class CreatePostRequest {

    /**
     * 帖子标题,必填,最长 100 字符
     */
    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 100, message = "帖子标题不能超过100个字符")
    private String title;

    /**
     * 帖子正文,必填,最长 500 字符
     */
    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 500, message = "帖子内容不能超过500个字符")
    private String content;
}
