package com.nikhil.mail_service.config;

import com.nikhil.mail_service.model.UserRegistrationEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.exchange.user:user-exchange}")
    private String userExchange;

    @Value("${rabbitmq.queue.email:emailQueue}")
    private String emailQueue;

    @Value("${rabbitmq.routing-key.user:user.registered}")
    private String userRoutingKey;

    @Value("${rabbitmq.exchange.newsletter:newsletter-exchange}")
    private String newsletterExchange;

    @Value("${rabbitmq.queue.newsletter:newsletterQueue}")
    private String newsletterQueue;

    @Value("${rabbitmq.routing-key.newsletter:newsletter.send}")
    private String newsletterRoutingKey;

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

    // ── Newsletter Topology (Exchange, Queue, Binding) ────────────────────────

    @Bean
    public DirectExchange newsletterExchange() {
        return new DirectExchange(newsletterExchange);
    }

    @Bean
    public Queue newsletterQueue() {
        return QueueBuilder.durable(newsletterQueue).build();
    }

    @Bean
    public Binding newsletterBinding(Queue newsletterQueue, DirectExchange newsletterExchange) {
        return BindingBuilder.bind(newsletterQueue).to(newsletterExchange).with(newsletterRoutingKey);
    }

    // ── Converters and Infrastructure ──────────────────────────────────────────

    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultClassMapper classMapper = new DefaultClassMapper();

        // Security: Explicitly trust the package where your models live
        classMapper.setTrustedPackages("com.nikhil.mail_service.model");

        Map<String, Class<?>> idClassMapping = new HashMap<>();
        idClassMapping.put(
                "com.nikhil.user_service.UserRegistrationEvent",
                UserRegistrationEvent.class
        );
        classMapper.setIdClassMapping(idClassMapping);

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }
}