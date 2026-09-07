package org.example.simpleweibobackend.post.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
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
