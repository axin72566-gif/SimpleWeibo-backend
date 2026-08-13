package org.example.simpleweibobackend.outbox.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("outbox")
public class Outbox extends BaseEntity {

    private Long postId;

    private Long userId;

    private String status;
}
