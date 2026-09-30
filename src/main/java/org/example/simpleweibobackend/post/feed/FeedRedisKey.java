package org.example.simpleweibobackend.post.feed;

public class FeedRedisKey {

    /** 粉丝收件箱, ZSet: member=帖子ID, score=发帖时间戳(毫秒), 拼接粉丝用户ID */
    public static final String FEED_INBOX = "feed:inbox:";

    /** 大V发件箱, ZSet: member=帖子ID, score=发帖时间戳(毫秒), 拼接博主用户ID */
    public static final String FEED_OUTBOX = "feed:outbox:";
}
