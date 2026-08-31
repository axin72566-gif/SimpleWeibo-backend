package org.example.simpleweibobackend.vote.vo;

import cn.hutool.json.JSONException;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.vote.entity.VoteActivity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VoteActivityVO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 帖子ID列表
     */
    private List<Long> postIds;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    public static VoteActivityVO from(VoteActivity voteActivity) {
        List<Long> postIds;

        try {
            postIds = JSONUtil.toList(voteActivity.getPostIds(), Long.class);
        } catch (JSONException e) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "帖子ID列表解析失败");
        }

        return new VoteActivityVO(voteActivity.getId(), postIds, voteActivity.getCreateTime());
    }
}
