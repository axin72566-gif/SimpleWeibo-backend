package org.example.simpleweibobackend.user.follow;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.simpleweibobackend.common.BaseEntity;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_follow")
public class Follow extends BaseEntity {

    private Long followerId;

    private Long followedId;
}
