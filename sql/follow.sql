create table follow
(
    id           bigint unsigned auto_increment comment '关注关系ID'
        primary key,
    follower_id  bigint unsigned                     not null comment '关注者用户ID',
    following_id bigint unsigned                     not null comment '被关注者用户ID',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_follower_following
        unique (follower_id, following_id)
)
    comment '用户关注关系表' collate = utf8mb4_unicode_ci;
