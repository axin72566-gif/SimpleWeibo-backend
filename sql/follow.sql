create table follow
(
    id           bigint unsigned auto_increment comment '关注关系ID'
        primary key,
    fan_id  bigint unsigned                     not null comment '关注者用户ID',
    following_id bigint unsigned                     not null comment '被关注者用户ID',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户关注关系表' collate = utf8mb4_unicode_ci;

-- 关注关系唯一约束 + 覆盖 "我关注了谁" 的查重/删除
ALTER TABLE follow ADD UNIQUE KEY uk_fan_following (fan_id, following_id);

-- fanout 时按 following_id 查所有粉丝（selectFanIds），同时是覆盖索引（含 fan_id）
ALTER TABLE follow ADD KEY idx_following_id (following_id);