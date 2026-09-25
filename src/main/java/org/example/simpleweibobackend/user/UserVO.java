package org.example.simpleweibobackend.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 对外的脱敏用户信息,不含密码
 */
@Data
@AllArgsConstructor
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname());
    }
}
