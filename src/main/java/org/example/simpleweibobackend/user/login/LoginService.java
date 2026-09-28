package org.example.simpleweibobackend.user.login;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.example.simpleweibobackend.user.auth.UserContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserMapper userMapper;
    private final StringRedisTemplate stringRedisTemplate;

    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            log.error("登录失败, 用户不存在或密码错误: username={}", request.getUsername());
            throw new BizException(ErrorCode.LOGIN_FAILED);
        }
        String token = IdUtil.fastSimpleUUID();
        try {
            stringRedisTemplate.opsForValue()
                    .set(UserContext.LOGIN_TOKEN + token, String.valueOf(user.getId()), UserContext.TOKEN_TTL);
        } catch (Exception e) {
            log.error("登录失败, token 写入 Redis 失败: token={}", token, e);
            throw new BizException(ErrorCode.INTERNAL_ERROR);
        }
        return new LoginVO(token, UserVO.from(user));
    }
}
