package org.example.simpleweibobackend.post.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostLikeEvent {

    private Long postId;

    private Long userId;
}
