package org.example.simpleweibobackend.post.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.entity.Post;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostVO {

    /**
     * 帖子ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public static PostVO from(Post post) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getCreateTime());
    }
}
