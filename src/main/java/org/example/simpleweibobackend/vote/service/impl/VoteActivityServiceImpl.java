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
import org.example.simpleweibobackend.vote.mapper.VoteActivityMapper;
import org.example.simpleweibobackend.vote.service.VoteActivityService;
import org.example.simpleweibobackend.vote.vo.VoteActivityVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoteActivityServiceImpl implements VoteActivityService {

    private static final int POST_COUNT = 10;

    private final VoteActivityMapper voteActivityMapper;
    private final PostMapper postMapper;

    @Override
    public VoteActivityVO createVoteActivity(CreateVoteActivityRequest request) {
        List<Long> postIds = request.getPostIds();
        if (postIds == null || postIds.stream().filter(Objects::nonNull).distinct().count() != POST_COUNT) {
            throw new BizException(ErrorCode.BAD_REQUEST, "投票活动必须包含10个不重复的帖子");
        }
        long count = postMapper.selectCount(new LambdaQueryWrapper<Post>().in(Post::getId, postIds));
        if (count != POST_COUNT) {
            throw new BizException(ErrorCode.NOT_FOUND, "存在不存在的帖子");
        }
        VoteActivity voteActivity = new VoteActivity();
        voteActivity.setPostIds(JSONUtil.toJsonStr(postIds));
        voteActivityMapper.insert(voteActivity);
        return VoteActivityVO.from(voteActivity);
    }

    @Override
    public VoteActivityVO getVoteActivity(Long id) {
        VoteActivity voteActivity = voteActivityMapper.selectById(id);
        if (voteActivity == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "投票活动不存在");
        }
        return VoteActivityVO.from(voteActivity);
    }
}
