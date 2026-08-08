package org.example.simpleweibobackend.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.user.entity.User;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public List<User> listAll() {
        return userMapper.selectList(null);
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectOne(new QueryWrapper<User>().eq("id", id));
    }
}
