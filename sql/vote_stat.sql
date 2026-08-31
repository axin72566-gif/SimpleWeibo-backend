create table vote_stat
(
    id          bigint unsigned auto_increment comment '记录ID'
        primary key,
    activity_id bigint unsigned                     not null comment '活动ID',
    post_id     bigint unsigned                     not null comment '帖子ID',
    vote_count  bigint unsigned                     not null default 0 comment '投票数',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '投票计数表(每个活动每个帖子一行,创建活动时初始化,对账任务重算)' collate = utf8mb4_unicode_ci;

ALTER TABLE vote_stat ADD UNIQUE KEY uk_activity_post (activity_id, post_id);
