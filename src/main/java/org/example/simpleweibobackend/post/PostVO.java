package org.example.simpleweibobackend.post;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostVO {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Integer auditStatus;

    private LocalDateTime createTime;

    public static PostVO from(Post post) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getAuditStatus(), post.getCreateTime());
    }
}