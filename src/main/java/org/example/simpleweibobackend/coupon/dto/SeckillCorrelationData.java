package org.example.simpleweibobackend.coupon.dto;

import lombok.Getter;
import org.springframework.amqp.rabbit.connection.CorrelationData;

/**
 * 秒杀消息关联数据：携带原始事件，供 publisher confirm 失败回调执行 Redis 补偿回滚
 */
@Getter
public class SeckillCorrelationData extends CorrelationData {

    private final CouponSeckillEvent event;

    public SeckillCorrelationData(String id, CouponSeckillEvent event) {
        super(id);
        this.event = event;
    }
}
