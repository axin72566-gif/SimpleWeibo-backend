package org.example.simpleweibobackend.post.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "发布帖子请求")
public class CreatePostRequest {

    @Schema(description = "帖子标题", example = "今日穿搭分享")
    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 100, message = "帖子标题不能超过100个字符")
    private String title;

    @Schema(description = "帖子内容", example = "今天天气真好")
    @NotBlank(message = "帖子内容不能为空")
    @Size(max = 500, message = "帖子内容不能超过500个字符")
    private String content;
}
