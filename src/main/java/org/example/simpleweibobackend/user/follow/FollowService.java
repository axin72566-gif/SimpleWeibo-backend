package org.example.simpleweibobackend.user.follow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.UserMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    /** 关注用户 */
    public void follow(Long userId, Long followedId) {
        if (userId.equals(followedId)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "不能关注自己");
        }
        if (userMapper.selectById(followedId) == null) {
            log.info("关注失败, 用户不存在: userId={}, followedId={}", userId, followedId);
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        int rows = followMapper.insertIgnore(userId, followedId);
        if (rows == 0) {
            log.info("重复关注: userId={}, followedId={}", userId, followedId);
            throw new BizException(ErrorCode.REPEAT_FOLLOW);
        }
    }
}
