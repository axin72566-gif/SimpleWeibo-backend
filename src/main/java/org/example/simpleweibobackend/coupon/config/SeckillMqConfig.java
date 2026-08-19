package org.example.simpleweibobackend.coupon.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeckillMqConfig {

    public static final String EXCHANGE = "coupon.seckill.exchange";
    public static final String QUEUE = "coupon.seckill.queue";
    public static final String ROUTING_KEY = "coupon.seckill";

    @Bean
    public DirectExchange couponSeckillExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue couponSeckillQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean
    public Binding couponSeckillBinding() {
        return BindingBuilder.bind(couponSeckillQueue()).to(couponSeckillExchange()).with(ROUTING_KEY);
    }

    // MQ消息JSON序列化
    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
