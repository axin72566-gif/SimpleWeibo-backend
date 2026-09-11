package org.example.simpleweibobackend.post.hot.recall;

import org.example.simpleweibobackend.post.Post;

import java.util.List;

/**
 * 召回层：从不同数据源获取有资格进入热榜的候选微博。
 */
public interface HotPostRecaller {

    List<Post> recall(RecallRequest request);
}
