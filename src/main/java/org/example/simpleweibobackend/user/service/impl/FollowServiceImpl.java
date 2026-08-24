package org.example.simpleweibobackend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.entity.Follow;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.FollowMapper;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.user.service.FollowService;
import org.example.simpleweibobackend.common.util.UserContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    @Override
    public void follow(Long followingId) {
        Long fanId = UserContext.getUserId();
        if (fanId.equals(followingId)) {
            throw new BizException(ErrorCode.BAD_REQUEST, "不能关注自己");
        }
        if (!userMapper.exists(new QueryWrapper<User>().eq("id", followingId))) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        boolean exists = followMapper.exists(new QueryWrapper<Follow>()
                .eq("fan_id", fanId)
                .eq("following_id", followingId));
        if (exists) {
            throw new BizException(ErrorCode.CONFLICT, "已关注该用户");
        }
        Follow follow = new Follow();
        follow.setFanId(fanId);
        follow.setFollowingId(followingId);
        followMapper.insert(follow);
    }

    @Override
    public void unfollow(Long followingId) {
        Long fanId = UserContext.getUserId();
        int deleted = followMapper.delete(new QueryWrapper<Follow>()
                .eq("fan_id", fanId)
                .eq("following_id", followingId));
        if (deleted == 0) {
            throw new BizException(ErrorCode.NOT_FOUND, "未关注该用户");
        }
    }
}
