package org.example.simpleweibobackend.post.like.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 点赞事件 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeEvent {

    private Long postId;

    private Long userId;
}
