package org.example.simpleweibobackend.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.entity.User;

@Data
@AllArgsConstructor
public class LoginVO {

    private String token;

    private Long id;

    private String username;

    private String nickname;

    public static LoginVO from(User user, String token) {
        return new LoginVO(token, user.getId(), user.getUsername(), user.getNickname());
    }
}
