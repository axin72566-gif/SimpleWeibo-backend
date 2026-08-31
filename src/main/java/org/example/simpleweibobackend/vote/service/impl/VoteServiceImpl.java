package org.example.simpleweibobackend.vote.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.vote.dto.CastVoteRequest;
import org.example.simpleweibobackend.vote.entity.VoteRecord;
import org.example.simpleweibobackend.vote.entity.VoteStat;
import org.example.simpleweibobackend.vote.mapper.VoteRecordMapper;
import org.example.simpleweibobackend.vote.mapper.VoteStatMapper;
import org.example.simpleweibobackend.vote.service.VoteService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {

    private final VoteRecordMapper voteRecordMapper;
    private final VoteStatMapper voteStatMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void castVote(CastVoteRequest request) {
        Long userId = UserContext.getUserId();

        VoteRecord record = new VoteRecord();
        record.setActivityId(request.getActivityId());
        record.setUserId(userId);
        record.setPostId(request.getPostId());
        try {
            voteRecordMapper.insert(record);
        } catch (DuplicateKeyException e) {
            throw new BizException(ErrorCode.CONFLICT, "已投过票，不能重复投票");
        }

        // 数据库端自增累加，不能查出来加一再写回，否则并发下丢票
        int updatedRows = voteStatMapper.update(null, new LambdaUpdateWrapper<VoteStat>()
                .eq(VoteStat::getActivityId, request.getActivityId())
                .eq(VoteStat::getPostId, request.getPostId())
                .setSql("vote_count = vote_count + 1"));
        if (updatedRows != 1) {
            throw new BizException(ErrorCode.NOT_FOUND, "投票活动不存在或帖子不属于该活动");
        }
    }
}
