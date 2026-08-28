package org.example.simpleweibobackend.vote.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.simpleweibobackend.common.BaseEntity;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "vote_activity", autoResultMap = true)
public class VoteActivity extends BaseEntity {

    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<Long> postIds;
}
