package org.example.simpleweibobackend.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserVO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 昵称
     */
    private String nickname;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname());
    }
}
