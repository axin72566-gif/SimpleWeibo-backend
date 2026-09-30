package org.example.simpleweibobackend.user.follow;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.PageVO;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FollowService {

    private static final int MAX_PAGE_SIZE = 100;

    private final FollowMapper followMapper;
    private final UserMapper userMapper;

    /** 关注用户 */
    public void follow(Long userId, Long followedId) {
        if (userId.equals(followedId)) {
            throw new BizException(ErrorCode.CANNOT_FOLLOW_SELF);
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

    /** 取消关注 */
    public void unfollow(Long userId, Long followedId) {
        if (userMapper.selectById(followedId) == null) {
            log.info("取消关注失败, 用户不存在: userId={}, followedId={}", userId, followedId);
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        int rows = followMapper.delete(new LambdaQueryWrapper<Follow>()
                .eq(Follow::getFollowerId, userId)
                .eq(Follow::getFollowedId, followedId));
        if (rows == 0) {
            log.info("取消关注失败, 未关注该用户: userId={}, followedId={}", userId, followedId);
            throw new BizException(ErrorCode.FOLLOW_NOT_FOUND);
        }
    }

    /** 分页查询我的关注列表,按关注时间倒序 */
    public PageVO<UserVO> listFollowings(Long userId, int page, int size) {
        page = Math.max(page, 1);
        size = Math.clamp(size, 1, MAX_PAGE_SIZE);
        Page<Follow> result = followMapper.selectPage(new Page<>(page, size),
                new LambdaQueryWrapper<Follow>()
                        .eq(Follow::getFollowerId, userId)
                        .orderByDesc(Follow::getCreateTime, Follow::getId));
        List<Long> followedIds = result.getRecords().stream().map(Follow::getFollowedId).toList();
        if (followedIds.isEmpty()) {
            return PageVO.of(List.of(), result.getTotal(), page, size);
        }
        Map<Long, User> users = userMapper.selectByIds(followedIds).stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        List<UserVO> records = followedIds.stream()
                .map(id -> UserVO.from(users.get(id)))
                .toList();
        return PageVO.of(records, result.getTotal(), page, size);
    }
}
