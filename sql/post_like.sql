create table post_like
(
    id          bigint unsigned auto_increment comment '点赞记录ID'
        primary key,
    post_id     bigint unsigned                    not null comment '帖子ID',
    user_id     bigint unsigned                    not null comment '点赞用户ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '帖子点赞关系表' collate = utf8mb4_unicode_ci;
