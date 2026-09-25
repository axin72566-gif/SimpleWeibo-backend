package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

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
@TableName("sensitive_word")
public class SensitiveWord extends BaseEntity {

    /**
     * 唯一
     */
    private String word;
}
