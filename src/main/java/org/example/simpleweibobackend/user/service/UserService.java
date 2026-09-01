package org.example.simpleweibobackend.user.service;

import org.example.simpleweibobackend.user.dto.RegisterRequest;
import org.example.simpleweibobackend.user.vo.RegisterVO;

public interface UserService {

    RegisterVO register(RegisterRequest request);
}
