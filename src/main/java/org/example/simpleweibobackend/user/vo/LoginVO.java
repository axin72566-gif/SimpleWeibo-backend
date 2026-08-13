package org.example.simpleweibobackend.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.common.Role;
import org.example.simpleweibobackend.user.entity.User;

@Data
@AllArgsConstructor
@Schema(description = "登录响应")
public class LoginVO {

    @Schema(description = "JWT令牌")
    private String token;

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "用户角色")
    private Role role;

    public static LoginVO from(User user, String token) {
        return new LoginVO(token, user.getId(), user.getUsername(), user.getNickname(), user.getRole());
    }
}
