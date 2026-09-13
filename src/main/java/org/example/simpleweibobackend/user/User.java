package org.example.simpleweibobackend.user;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.simpleweibobackend.common.BaseEntity;

/**
 * 用户实体,对应 user 表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User extends BaseEntity {

    /**
     * 用户名,登录凭证
     */
    private String username;

    /**
     * 加盐 SHA1 摘要后的密码,不存明文
     */
    private String password;

    /**
     * 昵称,注册时随机生成,用于页面展示
     */
    private String nickname;
}
