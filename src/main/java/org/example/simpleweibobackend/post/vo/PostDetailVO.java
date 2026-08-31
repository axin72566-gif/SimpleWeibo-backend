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

    /**
     * 作者
     */
    private UserVO author;

    public static PostDetailVO from(Post post, User author) {
        return new PostDetailVO(post.getId(), post.getUserId(),
                post.getTitle(), post.getContent(), post.getCreateTime(),
                author != null ? UserVO.from(author) : null);
    }
}
