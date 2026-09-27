package org.example.simpleweibobackend.user.register;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.common.ErrorCode;
import org.example.simpleweibobackend.common.exception.BizException;
import org.example.simpleweibobackend.user.User;
import org.example.simpleweibobackend.user.UserMapper;
import org.example.simpleweibobackend.user.UserVO;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final UserMapper userMapper;

    public UserVO register(RegisterRequest request) {
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, request.getUsername()));
        if (count > 0) {
            throw new BizException(ErrorCode.CONFLICT, "用户名已存在");
        }
        User user = User.builder()
                .username(request.getUsername())
                // BCrypt 随机加盐慢哈希,不存明文
                .password(BCrypt.hashpw(request.getPassword()))
                .nickname("user_" + RandomUtil.randomString(6))
                .build();
        try {
            userMapper.insert(user);
        } catch (DuplicateKeyException e) {
            // 查重后并发注册撞唯一索引的兜底
            throw new BizException(ErrorCode.CONFLICT, "用户名已存在");
        }
        return UserVO.from(user);
    }
}
