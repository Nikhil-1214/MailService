package com.nikhil.user_service.config;

import org.springframework.amqp.core.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    // ── Variables from application.yaml (with fallbacks) ──────────────────────

    @Value("${rabbitmq.exchange.user:user-exchange}")
    private String userExchange;

    @Value("${rabbitmq.queue.email:emailQueue}")
    private String emailQueue;

    @Value("${rabbitmq.routing-key.user:user.registered}")
    private String userRoutingKey;

    // ── RabbitMQ Topology ─────────────────────────────────────────────────────

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange(userExchange);
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable(emailQueue).build();
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(emailQueue).to(userExchange).with(userRoutingKey);
    }

    // ── Message Conversion (Java Objects to JSON) ─────────────────────────────

    @Bean
    public MessageConverter jsonMessageConverter() {
        // Explicitly use ObjectMapper for safer JSON mapping
        ObjectMapper mapper = new ObjectMapper();
        DefaultClassMapper classMapper = new DefaultClassMapper();
        classMapper.setTrustedPackages(
                "com.nikhil.user_service",
                "com.nikhil.user_service.model",
                "com.nikhil.mail_service.model"
        );
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}