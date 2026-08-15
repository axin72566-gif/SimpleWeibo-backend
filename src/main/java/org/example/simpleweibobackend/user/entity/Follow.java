package org.example.simpleweibobackend.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("follow")
public class Follow extends BaseEntity {

    private Long followerId;

    private Long followingId;
}
