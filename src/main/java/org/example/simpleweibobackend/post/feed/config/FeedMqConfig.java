package org.example.simpleweibobackend.post.feed.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeedMqConfig {

    public static final String EXCHANGE = "feed.fanout.exchange";
    public static final String QUEUE = "feed.fanout.queue";
    public static final String ROUTING_KEY = "post.created";
    public static final String DLQ = "feed.fanout.dlq";

    @Bean
    public DirectExchange feedExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    public Queue feedQueue() {
        return QueueBuilder.durable(QUEUE)
                .withArgument("x-dead-letter-exchange", "")
                .withArgument("x-dead-letter-routing-key", DLQ)
                .build();
    }

    @Bean
    public Queue feedDlq() {
        return QueueBuilder.durable(DLQ).build();
    }

    @Bean
    public Binding feedBinding() {
        return BindingBuilder.bind(feedQueue()).to(feedExchange()).with(ROUTING_KEY);
    }

    @Bean
    public JacksonJsonMessageConverter messageConverter() {
        return new JacksonJsonMessageConverter();
    }
}
