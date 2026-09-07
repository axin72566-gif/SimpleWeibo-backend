package org.example.simpleweibobackend.user.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.User;

@Data
@AllArgsConstructor
public class RegisterVO {

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

    public static RegisterVO from(User user) {
        return new RegisterVO(user.getId(), user.getUsername(), user.getNickname());
    }
}
