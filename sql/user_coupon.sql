create table user_coupon
(
    id          bigint unsigned auto_increment comment '主键ID'
        primary key,
    user_id     bigint                             not null comment '用户ID',
    coupon_id   bigint                             not null comment '优惠券ID',
    status      varchar(20)                        not null default 'UNUSED' comment '状态：UNUSED-未使用 USED-已使用',
    create_time datetime default CURRENT_TIMESTAMP null comment '领取时间',
    constraint uk_user_coupon unique (user_id, coupon_id)
) comment '用户领券记录表' collate = utf8mb4_unicode_ci;
