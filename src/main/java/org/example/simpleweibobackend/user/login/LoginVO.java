package org.example.simpleweibobackend.user.login;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.UserVO;

/**
 * 登录结果:token 由前端保存,后续请求通过 Authorization: Bearer {token} 携带
 */
@Data
@AllArgsConstructor
public class LoginVO {

    private String token;

    private UserVO user;
}
