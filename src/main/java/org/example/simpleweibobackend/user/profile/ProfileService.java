package org.example.simpleweibobackend.user.profile;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserMapper userMapper;

    public UserVO me() {
        User user = userMapper.selectById(UserContext.getUserId());
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return UserVO.from(user);
    }
}
