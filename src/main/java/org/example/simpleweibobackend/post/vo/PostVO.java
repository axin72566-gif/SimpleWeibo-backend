package org.example.simpleweibobackend.post.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.entity.Post;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "帖子信息")
public class PostVO {

    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "发布者用户ID")
    private Long userId;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    public static PostVO from(Post post) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getContent(), post.getCreateTime());
    }
}
