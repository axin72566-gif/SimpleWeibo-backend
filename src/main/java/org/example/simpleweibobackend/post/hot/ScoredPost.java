package org.example.simpleweibobackend.post.hot;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.post.Post;

/**
 * 热度计算层的输出，也是排序层的输入。
 */
@Data
@AllArgsConstructor
public class ScoredPost {

    /**
     * 帖子
     */
    private Post post;

    /**
     * 热度分数
     */
    private double score;
}
