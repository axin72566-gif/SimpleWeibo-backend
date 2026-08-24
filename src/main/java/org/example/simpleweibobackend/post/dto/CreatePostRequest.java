package org.example.simpleweibobackend.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreatePostRequest {

    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 100, message = "帖子标题不能超过100个字符")
    private String title;

    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 500, message = "帖子内容不能超过500个字符")
    private String content;
}
