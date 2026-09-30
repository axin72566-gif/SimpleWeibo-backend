package org.example.simpleweibobackend.post.feed.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 帖子推送事件 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedPushEvent {

    private Long postId;

    private Long publisherId;

    /** 发帖时间戳(毫秒), 作为收件箱 ZSet 的 score */
    private Long createTimeMillis;
}
