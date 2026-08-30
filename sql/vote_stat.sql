CREATE TABLE vote_stat
(
    id          bigint unsigned AUTO_INCREMENT COMMENT '记录ID'
        PRIMARY KEY,
    activity_id bigint unsigned NOT NULL COMMENT '活动ID',
    post_id     bigint unsigned NOT NULL COMMENT '帖子ID',
    vote_count  bigint unsigned NOT NULL DEFAULT 0 COMMENT '投票数',
    create_time datetime DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)
    COMMENT '投票计数表(每个活动每个帖子一行,创建活动时初始化)' COLLATE = utf8mb4_unicode_ci;
