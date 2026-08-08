package org.example.simpleweibobackend.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.entity.User;

@Data
@AllArgsConstructor
public class UserVO {

    private Long id;

    private String username;

    private String nickname;

    private String bio;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getBio());
    }
}
