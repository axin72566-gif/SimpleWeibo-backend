create table feed_item
(
    id           bigint unsigned auto_increment comment 'Feed项ID'
        primary key,
    user_id      bigint unsigned                     not null comment '收件人用户ID(粉丝)',
    post_id      bigint unsigned                     not null comment '帖子ID',
    post_user_id bigint unsigned                     not null comment '发帖人用户ID',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    index idx_user_create (user_id, create_time desc)
)
    comment 'Feed收件箱表' collate = utf8mb4_unicode_ci;
