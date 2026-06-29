package com.nikhil.mail_service; // Ensure this is in a package scanned by @SpringBootApplication

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public MessageConverter jsonMessageConverter() {
        // This is the "Magic" that fixes your MessageConversionException
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public DirectExchange userExchange() {
        return new DirectExchange("user-exchange");
    }

    @Bean
    public Queue emailQueue() {
        return new Queue("emailQueue");
    }

    @Bean
    public Binding binding(Queue emailQueue, DirectExchange userExchange) {
        return BindingBuilder.bind(emailQueue).to(userExchange).with("user.registered");
    }
}