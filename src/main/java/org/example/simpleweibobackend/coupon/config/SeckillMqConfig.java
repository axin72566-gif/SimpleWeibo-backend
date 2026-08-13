package org.example.simpleweibobackend.coupon.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillMqConfig {

    public static final String EXCHANGE = "coupon.seckill.exchange";
    public static final String QUEUE = "coupon.seckill.queue";
    public static final String ROUTING_KEY = "coupon.seckill";
    public static final String DLQ = "coupon.seckill.dlq";

    @Bean
    public DirectExchange couponSeckillExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue couponSeckillQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue couponSeckillDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding couponSeckillBinding() {
        return BindingBuilder.bind(couponSeckillQueue()).to(couponSeckillExchange()).with(ROUTING_KEY);
    }
}
