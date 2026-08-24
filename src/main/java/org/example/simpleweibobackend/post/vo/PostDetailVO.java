package org.example.simpleweibobackend.post.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.vo.UserVO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostDetailVO {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private UserVO author;

    public static PostDetailVO from(Post post, User author) {
        return new PostDetailVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getCreateTime(),
                author != null ? UserVO.from(author) : null);
    }
}
