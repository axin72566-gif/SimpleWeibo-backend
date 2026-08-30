CREATE TABLE vote_record
(
    id          bigint unsigned AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    activity_id bigint unsigned NOT NULL COMMENT '活动ID',
    user_id     bigint unsigned NOT NULL COMMENT '投票用户ID',
    post_id     bigint unsigned NOT NULL COMMENT '被投帖子ID',
    create_time datetime DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)
    COMMENT '投票记录表(一人一活动仅一条)' COLLATE = utf8mb4_unicode_ci;
