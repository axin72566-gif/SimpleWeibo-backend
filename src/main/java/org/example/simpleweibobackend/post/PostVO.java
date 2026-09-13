package org.example.simpleweibobackend.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 帖子视图对象
 */
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
     * 访问量
     */
    private Long viewCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 由帖子实体转换为视图对象
     */
    public static PostVO from(Post post) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getViewCount(), post.getCreateTime());
    }
}
