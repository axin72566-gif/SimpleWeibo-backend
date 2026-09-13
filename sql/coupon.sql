create table coupon
(
    id              bigint unsigned auto_increment comment '优惠券ID'
        primary key,
    title           varchar(50)                          not null comment '优惠券名称',
    total_count     bigint      default 0                not null comment '发放总量（0为不限量）',
    create_time     datetime    default CURRENT_TIMESTAMP null comment '创建时间',
    update_time     datetime    default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '优惠券表' collate = utf8mb4_unicode_ci;
