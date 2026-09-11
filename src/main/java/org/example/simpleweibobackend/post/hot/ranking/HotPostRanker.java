package org.example.simpleweibobackend.post.hot.ranking;

import org.example.simpleweibobackend.post.Post;
import org.example.simpleweibobackend.post.hot.ScoredPost;

import java.util.List;

/**
 * 排序层：对已计算热度的微博重排并截取榜单。
 */
public interface HotPostRanker {

    List<Post> rank(List<ScoredPost> scoredPosts);
}
