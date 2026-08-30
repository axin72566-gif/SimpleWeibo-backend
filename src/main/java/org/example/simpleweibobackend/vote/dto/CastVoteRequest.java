package org.example.simpleweibobackend.vote.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CastVoteRequest {

    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "帖子ID不能为空")
    private Long postId;
}
