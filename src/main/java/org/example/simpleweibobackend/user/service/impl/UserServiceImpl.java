package org.example.simpleweibobackend.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.simpleweibobackend.user.mapper.UserMapper;
import org.example.simpleweibobackend.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

}
