package org.example.simpleweibobackend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.exception.BizException;
import org.example.simpleweibobackend.user.dto.LoginRequest;
import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.user.service.UserService;
import org.example.simpleweibobackend.user.vo.LoginVO;
import org.example.simpleweibobackend.user.vo.RegisterVO;
import org.example.simpleweibobackend.user.vo.UserVO;
import org.example.simpleweibobackend.util.JwtUtil;
import org.example.simpleweibobackend.util.PasswordUtil;
import org.example.simpleweibobackend.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    @Override
    public RegisterVO register(RegisterRequest request) {
        boolean exists = userMapper.exists(new QueryWrapper<User>()
                .eq("username", request.getUsername()));
        if (exists) {
            throw new BizException(ErrorCode.CONFLICT, "用户名已存在");
        }

        String nickname = "user_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hash(request.getPassword()));
        user.setNickname(nickname);
        userMapper.insert(user);

        return RegisterVO.from(user);
    }

    @Override
    public LoginVO login(LoginRequest request) {
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .eq("username", request.getUsername()));
        if (user == null || !PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }
        String token = jwtUtil.generateToken(user.getId(), user.getRole());
        return LoginVO.from(user, token);
    }

    @Override
    public void logout(String token) {
        jwtUtil.blacklist(token);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return UserVO.from(user);
    }
}
