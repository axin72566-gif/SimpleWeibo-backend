create table post
(
    id          bigint unsigned auto_increment comment '帖子ID'
        primary key,
    user_id     bigint unsigned                     not null comment '发布者用户ID',
    title       varchar(100)                        not null comment '帖子标题',
    content     varchar(500)                        not null comment '帖子正文',
    view_count  bigint default 0                    not null comment '访问量',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '帖子表' collate = utf8mb4_unicode_ci;
