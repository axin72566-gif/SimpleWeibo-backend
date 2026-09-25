package org.example.simpleweibobackend.user.register;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.data.redis.core.script.DigestUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    /**
     * 密码加盐,防彩虹表反查
     */
    private static final String SALT = "simple_weibo_2026";

    private final UserMapper userMapper;

    public UserVO register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .password(DigestUtils.sha1DigestAsHex(request.getPassword() + SALT))
                .nickname("user_" + RandomUtil.randomString(6))
                .build();
        userMapper.insert(user);
        return UserVO.from(user);
    }
}
