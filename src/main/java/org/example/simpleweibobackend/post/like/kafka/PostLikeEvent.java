package org.example.simpleweibobackend.post.like.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 点赞事件 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeEvent {

    private Long postId;

    private Long userId;

    /** 不设置时默认为点赞 */
    @Builder.Default
    private PostLikeEventType type = PostLikeEventType.LIKE;
}
