package org.example.simpleweibobackend.common.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis-Plus 字段自动填充器:配合 {@link org.example.simpleweibobackend.common.BaseEntity} 的
 * TableField(fill = Fill.INSERT_UPDATE) 注解,插入/更新时自动写入 createTime/updateTime,
 * 业务代码无需手动赋值
 */
@Component
public class MyMetaObjectHandlerConfig implements MetaObjectHandler {

    /**
     * 插入时填充创建时间和更新时间
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }

    /**
     * 更新时仅刷新更新时间
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
