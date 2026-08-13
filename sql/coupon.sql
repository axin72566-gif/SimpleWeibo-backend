create table coupon
(
    id              bigint unsigned auto_increment comment '优惠券ID'
        primary key,
    name            varchar(100)                        not null comment '优惠券名称',
    discount_rate   tinyint unsigned                    not null comment '折扣率(1-99)，如80表示8折',
    total_quantity  int                                 not null comment '发行总量',
    start_time      datetime                            not null comment '生效开始时间',
    end_time        datetime                            not null comment '生效结束时间',
    status          varchar(20)                         not null default 'DRAFT' comment '状态：DRAFT-草稿 PUBLISHED-已发布 OFFLINE-已下架',
    create_time     datetime default CURRENT_TIMESTAMP  null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP  null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '优惠券表' collate = utf8mb4_unicode_ci;
