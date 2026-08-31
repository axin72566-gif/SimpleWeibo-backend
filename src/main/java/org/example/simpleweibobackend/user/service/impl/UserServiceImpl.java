package org.example.simpleweibobackend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.user.service.UserService;
import org.example.simpleweibobackend.user.vo.RegisterVO;
import org.example.simpleweibobackend.user.vo.UserVO;
import org.example.simpleweibobackend.common.util.PasswordUtil;
import org.example.simpleweibobackend.common.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public RegisterVO register(RegisterRequest request) {
        // 校验用户名是否存在
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
        // 保存用户
        userMapper.insert(user);

        return RegisterVO.from(user);
    }

    @Override
    public UserVO getCurrentUser() {
        Long userId = UserContext.getUserId();
        // 查询用户
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.NOT_FOUND, "用户不存在");
        }
        return UserVO.from(user);
    }
}
