create table vote_record
(
    id          bigint unsigned auto_increment comment '记录ID'
        primary key,
    activity_id bigint unsigned                     not null comment '活动ID',
    user_id     bigint unsigned                     not null comment '投票用户ID',
    post_id     bigint unsigned                     not null comment '被投帖子ID',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '投票记录表(一人一活动仅一条)' collate = utf8mb4_unicode_ci;