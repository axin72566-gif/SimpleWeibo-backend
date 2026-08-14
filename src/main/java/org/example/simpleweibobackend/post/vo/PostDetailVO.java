package org.example.simpleweibobackend.post.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.vo.UserVO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Schema(description = "帖子详情")
public class PostDetailVO {

    @Schema(description = "帖子ID")
    private Long id;

    @Schema(description = "发布者用户ID")
    private Long userId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子内容")
    private String content;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "作者信息")
    private UserVO author;

    public static PostDetailVO from(Post post, User author) {
        return new PostDetailVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getCreateTime(),
                author != null ? UserVO.from(author) : null);
    }
}
