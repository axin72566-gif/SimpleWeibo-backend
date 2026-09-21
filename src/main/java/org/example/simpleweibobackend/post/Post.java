package org.example.simpleweibobackend.post;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.simpleweibobackend.common.BaseEntity;

/**
 * 帖子实体,对应 post 表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("post")
public class Post extends BaseEntity {

    /**
     * 发布者用户 ID
     */
    private Long userId;

    /**
     * 帖子标题,最长 100 字符
     */
    private String title;

    /**
     * 帖子正文,最长 500 字符
     */
    private String content;

}
