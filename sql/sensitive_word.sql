create table sensitive_word
(
    id          bigint unsigned auto_increment comment '敏感词ID'
        primary key,
    word        varchar(50)                        not null comment '敏感词',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '敏感词表' collate = utf8mb4_unicode_ci;
