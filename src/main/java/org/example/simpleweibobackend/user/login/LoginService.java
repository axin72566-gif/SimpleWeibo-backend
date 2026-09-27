package org.example.simpleweibobackend.user.login;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        // 用户不存在与密码错误返回同一提示,防止用户名枚举
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        String token = IdUtil.fastSimpleUUID();
        stringRedisTemplate.opsForValue()
                .set(UserContext.TOKEN_KEY_PREFIX + token, String.valueOf(user.getId()), UserContext.TOKEN_TTL);
        return new LoginVO(token, UserVO.from(user));
    }
}
