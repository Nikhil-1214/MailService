package com.nikhil.mail_service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

        // 2. Set strict timeouts (in milliseconds)
        factory.setConnectTimeout(5000); // Give up if it takes more than 5 seconds to connect
        factory.setReadTimeout(10000);   // Give up if it takes more than 10 seconds to read the data

        // 3. Return the newly configured safety RestTemplate
        return new RestTemplate(factory);
    }
}