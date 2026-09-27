create table post_like
(
    id          bigint unsigned auto_increment comment '点赞ID'
        primary key,
    user_id     bigint unsigned                    not null comment '点赞用户ID',
    post_id     bigint unsigned                    not null comment '帖子ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_user_post unique (user_id, post_id),
    index idx_post (post_id)
)
    comment '帖子点赞表' collate = utf8mb4_unicode_ci;
