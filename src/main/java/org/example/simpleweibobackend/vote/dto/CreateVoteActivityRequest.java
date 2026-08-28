package org.example.simpleweibobackend.vote.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateVoteActivityRequest {

    @NotNull(message = "帖子ID列表不能为空")
    @Size(min = 10, max = 10, message = "投票活动必须包含10个帖子")
    private List<Long> postIds;
}
