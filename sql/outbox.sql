create table outbox
(
    id          bigint unsigned auto_increment comment '消息ID'
        primary key,
    post_id     bigint unsigned                     not null comment '帖子ID',
    user_id     bigint unsigned                     not null comment '发帖人用户ID',
    status      varchar(20)  default 'PENDING'      not null comment '发送状态: PENDING/SENT',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_status_create (status, create_time)
)
    comment '本地消息表' collate = utf8mb4_unicode_ci;
