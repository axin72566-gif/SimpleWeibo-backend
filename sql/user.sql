create table user
(
    id          bigint unsigned auto_increment comment '用户ID'
        primary key,
    username    varchar(50)                        not null comment '用户名',
    password    varchar(100)                       not null comment '加密后的密码',
    nickname    varchar(50)                        null comment '昵称',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户表' collate = utf8mb4_unicode_ci;
