package org.example.simpleweibobackend.user.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.simpleweibobackend.user.entity.User;

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

    /**
     * 个人简介
     */
    private String bio;

    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname(), user.getBio());
    }
}
