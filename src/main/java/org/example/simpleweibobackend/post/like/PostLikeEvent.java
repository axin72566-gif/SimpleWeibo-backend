package org.example.simpleweibobackend.post.like;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostLikeEvent {

    /**
     * 帖子id
     */
    private Long postId;

    /**
     * 用户id
     */
    private Long userId;
}
