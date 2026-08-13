package org.example.simpleweibobackend.user.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.constant.Role;
import org.example.simpleweibobackend.user.entity.User;

@Data
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserVO {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "个人简介")
    private String bio;

    @Schema(description = "用户角色")
    private Role role;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getBio(), user.getRole());
    }
}
