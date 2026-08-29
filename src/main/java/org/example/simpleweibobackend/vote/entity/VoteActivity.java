package org.example.simpleweibobackend.vote.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("vote_activity")
public class VoteActivity extends BaseEntity {

    /**
     * 参与投票的帖子ID列表(固定10个，List<Long>序列化)
     */
    private String postIds;
}
