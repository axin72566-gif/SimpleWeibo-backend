package org.example.simpleweibobackend.post.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("inbox")
public class Inbox extends BaseEntity {

    private Long userId;

    private Long postId;

    private Long postUserId;
}
