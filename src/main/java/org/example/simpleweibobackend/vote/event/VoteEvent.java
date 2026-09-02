package org.example.simpleweibobackend.vote.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteEvent {

    /**
     * 投票活动ID
     */
    private Long activityId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 投票的帖子ID
     */
    private Long postId;
}
