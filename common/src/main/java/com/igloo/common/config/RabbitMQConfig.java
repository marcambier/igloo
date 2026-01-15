package com.igloo.common.config;

import java.util.List;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // TODO : externalize queue and routing keys in configuration file
    public static final String BEER_ORDER_QUEUE = "beer.ordered.queue";
    public static final String BEER_ORDER_TIMEOUT_QUEUE = "beer.ordered.timeout.queue";
    public static final String BEER_ORDER_ROUTING_KEY = "beer.ordered";
    public static final String STOCK_LOW_ROUTING_KEY = "stock.low";
    public static final String GO_BACK_HOME_ROUTING_KEY = "go.back.home";
    public static final String EXCHANGE = "igloo.exchange";

    @Bean
    public SimpleMessageConverter messageConverter() {
        SimpleMessageConverter converter = new SimpleMessageConverter();
        converter.setAllowedListPatterns(List.of("com.igloo.common.event.*", "java.time.*"));
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}