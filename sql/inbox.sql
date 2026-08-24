create table inbox
(
    id           bigint unsigned auto_increment comment '收件箱记录ID'
        primary key,
    receiver_id      bigint unsigned                     not null comment '收件人用户ID(粉丝)',
    post_id      bigint unsigned                     not null comment '帖子ID',
    author_id bigint unsigned                     not null comment '发帖人用户ID',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment 'Feed收件箱表' collate = utf8mb4_unicode_ci;
