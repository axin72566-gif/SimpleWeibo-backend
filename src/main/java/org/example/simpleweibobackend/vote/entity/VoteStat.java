package org.example.simpleweibobackend.vote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vote_stat")
public class VoteStat extends BaseEntity {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 帖子ID
     */
    private Long postId;

    /**
     * 投票数
     */
    private Long voteCount;
}
