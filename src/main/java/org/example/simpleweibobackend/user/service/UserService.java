package org.example.simpleweibobackend.user.service;

import org.example.simpleweibobackend.user.dto.LoginRequest;
import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.vo.LoginVO;
import org.example.simpleweibobackend.user.vo.RegisterVO;
import org.example.simpleweibobackend.user.vo.UserVO;

public interface UserService {

    RegisterVO register(RegisterRequest request);

    LoginVO login(LoginRequest request);

    void logout(String token);

    UserVO getCurrentUser();
}
