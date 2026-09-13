create table user_coupon
(
    id          bigint unsigned auto_increment comment '记录ID'
        primary key,
    user_id     bigint unsigned                     not null comment '用户ID',
    coupon_id   bigint unsigned                     not null comment '优惠券ID',
    create_time datetime    default CURRENT_TIMESTAMP null comment '领取时间',
    update_time datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户优惠券表' collate = utf8mb4_unicode_ci;