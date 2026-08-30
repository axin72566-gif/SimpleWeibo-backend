package org.example.simpleweibobackend.vote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vote_record")
public class VoteRecord extends BaseEntity {

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 投票用户ID
     */
    private Long userId;

    /**
     * 被投帖子ID
     */
    private Long postId;
}
