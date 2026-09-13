package org.example.simpleweibobackend.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 用户视图对象:对外的脱敏用户信息,不含密码
 */
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
     * 由实体转换为视图对象,剔除密码等敏感字段
     */
    public static UserVO from(User user) {
        return new UserVO(user.getId(), user.getUsername(), user.getNickname());
    }
}
