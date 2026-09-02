package org.example.simpleweibobackend.vote.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.common.util.UserContext;
import org.example.simpleweibobackend.vote.dto.CastVoteRequest;
import org.example.simpleweibobackend.vote.event.VoteEvent;
import org.example.simpleweibobackend.vote.queue.VoteEventQueue;
import org.example.simpleweibobackend.vote.service.VoteService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {

    private static final int VOTE_OTHER = 0;
    private static final int VOTE_SUCCESS = 1;
    private static final int VOTE_SAME = 2;
    private static final String USER_POST_KEY = "vote:activity:user:post:{%d}";
    private static final String POST_COUNT_KEY = "vote:activity:post:count:{%d}";
    private static final String DIRTY_USER_KEY = "vote:activity:dirty:user:{%d}";

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> castVoteScript;
    private final VoteEventQueue voteEventQueue;

    @Override
    public void castVote(CastVoteRequest request) {
        Long userId = UserContext.getUserId();
        Long activityId = request.getActivityId();
        Long postId = request.getPostId();
        VoteEvent event = new VoteEvent(activityId, userId, postId);

        Long result = stringRedisTemplate.execute(
                castVoteScript,
                List.of(
                        USER_POST_KEY.formatted(activityId),
                        POST_COUNT_KEY.formatted(activityId),
                        DIRTY_USER_KEY.formatted(activityId)),
                userId.toString(),
                postId.toString());

        if (result == null) {
            throw new BizException(ErrorCode.INTERNAL_ERROR, "投票处理失败");
        }

        if (result == VOTE_SUCCESS) {
            enqueueVoteEvent(event);
            return;
        }
        if (result == VOTE_SAME) {
            return;
        }
        if (result == VOTE_OTHER) {
            throw new BizException(ErrorCode.CONFLICT, "已投给其他帖子，不能修改投票");
        }
        throw new BizException(ErrorCode.INTERNAL_ERROR, "投票处理结果异常");
    }

    private void enqueueVoteEvent(VoteEvent event) {
        boolean enqueue = voteEventQueue.offer(event);
        if (!enqueue) {
            log.warn("投票事件队列已满，等待补偿任务处理，activityId={}, userId={}",
                    event.getActivityId(), event.getUserId());
        }
    }
}
