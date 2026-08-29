CREATE TABLE vote_activity
(
    id          bigint unsigned AUTO_INCREMENT COMMENT '活动ID'
        PRIMARY KEY,
    post_ids    varchar(255) NOT NULL COMMENT '参与投票的帖子ID列表(固定10个)',
    create_time datetime DEFAULT CURRENT_TIMESTAMP NULL COMMENT '创建时间',
    update_time datetime DEFAULT CURRENT_TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
)
    COMMENT '投票活动表' COLLATE = utf8mb4_unicode_ci;
