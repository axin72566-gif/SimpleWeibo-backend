create table vote_activity
(
    id          bigint unsigned auto_increment comment '活动ID'
        primary key,
    post_ids    json                                     not null comment '参与投票的帖子ID列表(固定10个)' check (json_type(post_ids) = 'ARRAY' and json_length(post_ids) = 10),
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '投票活动表' collate = utf8mb4_unicode_ci;
