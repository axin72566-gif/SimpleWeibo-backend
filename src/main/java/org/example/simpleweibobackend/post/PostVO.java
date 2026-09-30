package org.example.simpleweibobackend.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserVO;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class PostVO {

    private Long id;

    private Long userId;

    private String title;

    private String content;

    private Long likeCount;

    private LocalDateTime createTime;

    /** 帖子作者 */
    private UserVO author;

    public static PostVO from(Post post, User author) {
        return new PostVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(),
                post.getLikeCount() == null ? 0L : post.getLikeCount(),
                post.getCreateTime(),
                UserVO.from(author));
    }
}
