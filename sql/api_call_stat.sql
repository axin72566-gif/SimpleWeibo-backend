create table api_call_stat
(
    id                bigint unsigned auto_increment comment '统计ID' primary key,
    api_path          varchar(255)                     not null comment 'API路径',
    http_method       varchar(10)                      not null comment 'HTTP方法',
    controller_class  varchar(100)                     not null comment '控制器类名',
    controller_method varchar(100)                     not null comment '控制器方法名',
    call_count        bigint      default 0            not null comment '调用次数',
    last_call_time    datetime                         not null comment '最后调用时间',
    create_time       datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time       datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    constraint uk_api_path_method unique (api_path, http_method)
)
    comment '接口调用统计表' collate = utf8mb4_unicode_ci;
