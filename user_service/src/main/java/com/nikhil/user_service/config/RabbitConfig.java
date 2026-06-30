package com.nikhil.user_service.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        DefaultClassMapper classMapper=new DefaultClassMapper();
        classMapper.setTrustedPackages("com.nikhil.user_service.model", "com.nikhil.mail_service.model");
        Jackson2JsonMessageConverter converter=new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper);
        return converter;

    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template=new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange("user-exchange");
    }

    @Bean
    public Queue emailQueue() {
        return QueueBuilder.durable("emailQueue").build();
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(emailQueue).to(userExchange).with("user.registered");
    }
}