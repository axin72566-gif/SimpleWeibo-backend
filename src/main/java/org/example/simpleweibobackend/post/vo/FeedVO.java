package org.example.simpleweibobackend.post.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class FeedVO {

    private List<PostVO> items;

    /**
     * 下一页游标，为 null 表示没有更多数据
     */
    private Long nextCursor;
}
