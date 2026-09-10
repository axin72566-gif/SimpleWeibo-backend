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

    private static final String SALT = "simple_weibo_2026";

    private final UserMapper userMapper;

    public UserVO register(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(DigestUtils.sha1DigestAsHex(request.getPassword() + SALT));
        user.setNickname("user_" + RandomUtil.randomString(6));
        userMapper.insert(user);
        return UserVO.from(user);
    }
}
