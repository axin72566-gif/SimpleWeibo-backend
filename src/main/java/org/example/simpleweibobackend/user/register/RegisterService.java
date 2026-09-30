package org.example.simpleweibobackend.user.register;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserMapper userMapper;

    public UserVO register(RegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            log.warn("用户名 {} 已存在", request.getUsername());
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(BCrypt.hashpw(request.getPassword()))
                .nickname(request.getUsername())
                .build();
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            log.warn("并发注册撞唯一索引: username={}", request.getUsername(), e);
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }
        return UserVO.from(user);
    }
}