create table product
(
    id          BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
    name        VARCHAR(100)   NOT NULL COMMENT '商品名称',
    description TEXT COMMENT '商品描述',
    price       DECIMAL(10, 2) NOT NULL COMMENT '售价，单位：元',
    stock       INT            NOT NULL DEFAULT 0 COMMENT '库存',
    status      TINYINT        NOT NULL DEFAULT 0 COMMENT '0下架，1上架',
    created_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
)
    comment '商品表' collate = utf8mb4_unicode_ci;