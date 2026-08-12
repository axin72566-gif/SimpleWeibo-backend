package org.example.simpleweibobackend.feed.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("feed_item")
public class FeedItem extends BaseEntity {

    private Long userId;

    private Long postId;

    private Long postUserId;
}
