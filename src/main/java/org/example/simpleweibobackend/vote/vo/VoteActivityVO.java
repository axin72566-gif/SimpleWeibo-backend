package org.example.simpleweibobackend.vote.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.vote.entity.VoteActivity;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class VoteActivityVO {

    private Long id;

    private List<Long> postIds;

    private LocalDateTime createTime;

    public static VoteActivityVO from(VoteActivity voteActivity) {
        return new VoteActivityVO(voteActivity.getId(), voteActivity.getPostIds(),
                voteActivity.getCreateTime());
    }
}
