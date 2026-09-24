package org.example.simpleweibobackend.post.create.audit.handler.sensitiveword;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.example.simpleweibobackend.common.BaseEntity;

/**
 * 敏感词实体,对应 sensitive_word 表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sensitive_word")
public class SensitiveWord extends BaseEntity {

    /**
     * 敏感词,唯一
     */
    private String word;
}
