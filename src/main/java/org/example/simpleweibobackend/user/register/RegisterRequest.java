package org.example.simpleweibobackend.user.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 注册请求参数
 */
@Data
public class RegisterRequest {

    /**
     * 用户名,唯一登录凭证,2-20 个字符
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 20, message = "用户名长度需在2-20个字符之间")
    private String username;

    /**
     * 明文密码,6-50 个字符,服务端加盐摘要后存储
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度需在6-50个字符之间")
    private String password;
}
