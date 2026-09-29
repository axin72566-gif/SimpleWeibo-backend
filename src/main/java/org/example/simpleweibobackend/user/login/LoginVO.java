package org.example.simpleweibobackend.user.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.UserVO;

/** 登录结果 */
@Data
@AllArgsConstructor
public class LoginVO {

    private String token;

    private UserVO user;
}
