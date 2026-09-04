create table product_order
(
    id           BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
    user_id      BIGINT         NOT NULL COMMENT '购买用户ID',
    product_id   BIGINT         NOT NULL COMMENT '商品ID',
    product_name VARCHAR(100)   NOT NULL COMMENT '购买时的商品名称',
    unit_price   DECIMAL(10, 2) NOT NULL COMMENT '购买时的商品单价',
    quantity     INT            NOT NULL COMMENT '购买数量',
    total_amount DECIMAL(18, 2) NOT NULL COMMENT '订单总金额',
    status       TINYINT        NOT NULL DEFAULT 0 COMMENT '0待支付，1已支付，2已关闭',
    expire_time  DATETIME       NOT NULL COMMENT '支付截止时间',
    paid_time    DATETIME       NULL COMMENT '支付时间',
    closed_time  DATETIME       NULL COMMENT '关闭时间',
    created_at   DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP
)
    comment '商品订单表' collate = utf8mb4_unicode_ci;
