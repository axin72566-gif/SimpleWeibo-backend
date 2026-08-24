package org.example.simpleweibobackend.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.entity.User;

@Data
@AllArgsConstructor
public class RegisterVO {

    private Long id;

    private String username;

    private String nickname;

    public static RegisterVO from(User user) {
        return new RegisterVO(user.getId(), user.getUsername(), user.getNickname());
    }
}
