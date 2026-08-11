package org.example.simpleweibobackend.post.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.entity.Post;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostVO {

    private Long id;

    private Long userId;

    private String content;

    private LocalDateTime createTime;

    public static PostVO from(Post post) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getContent(), post.getCreateTime());
    }
}
