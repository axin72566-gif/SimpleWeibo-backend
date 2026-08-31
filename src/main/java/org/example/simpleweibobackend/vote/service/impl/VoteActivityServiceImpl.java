package org.example.simpleweibobackend.vote.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.post.entity.Post;
import org.example.simpleweibobackend.post.mapper.PostMapper;
import org.example.simpleweibobackend.vote.dto.CreateVoteActivityRequest;
import org.example.simpleweibobackend.vote.entity.VoteActivity;
import org.example.simpleweibobackend.vote.entity.VoteStat;
import org.example.simpleweibobackend.vote.mapper.VoteActivityMapper;
import org.example.simpleweibobackend.vote.mapper.VoteStatMapper;
import org.example.simpleweibobackend.vote.service.VoteActivityService;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteActivityServiceImpl implements VoteActivityService {

    private static final int POST_COUNT = 10;

    private final VoteActivityMapper voteActivityMapper;
    private final VoteStatMapper voteStatMapper;
    private final PostMapper postMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VoteActivityVO createVoteActivity(CreateVoteActivityRequest request) {
        List<Long> postIds = request.getPostIds();
        if (postIds.stream().distinct().count() != POST_COUNT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "投票活动不能包含重复帖子");
        }
        long count = postMapper.selectCount(new LambdaQueryWrapper<Post>().in(Post::getId, postIds));
        if (count != POST_COUNT) {
            throw new BizException(ErrorCode.NOT_FOUND, "存在不存在的帖子");
        }

        VoteActivity voteActivity = new VoteActivity();
        voteActivity.setPostIds(JSONUtil.toJsonStr(postIds));
        voteActivityMapper.insert(voteActivity);

        for (Long postId : postIds) {
            VoteStat voteStat = new VoteStat();
            voteStat.setActivityId(voteActivity.getId());
            voteStat.setPostId(postId);
            voteStat.setVoteCount(0L);
            voteStatMapper.insert(voteStat);
        }

        return VoteActivityVO.from(voteActivity);
    }

}
