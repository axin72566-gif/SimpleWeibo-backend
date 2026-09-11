package org.example.simpleweibobackend.post.hot.calculation;

import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.hot.ScoredPost;

import java.util.List;

/**
 * 热度计算层：将候选微博转换为带热度分数的微博。
 */
public interface HotPostCalculator {

    List<ScoredPost> calculate(List<Post> recallPosts);
}
