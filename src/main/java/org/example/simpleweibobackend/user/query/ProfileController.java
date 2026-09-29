package org.example.simpleweibobackend.user.query;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.Result;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserMapper userMapper;

    /** 查看当前登录用户的个人信息 */
    @GetMapping("/me")
    public Result<UserVO> me() {
        User user = userMapper.selectById(UserContext.getUserId());
        if (user == null) {
            log.error("获取个人信息失败, 当前登录用户不存在: userId={}", UserContext.getUserId());
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        return Result.success(UserVO.from(user));
    }
}
