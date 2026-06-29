package com.nikhil.mail_service;

import com.nikhil.mail_service.model.Content;
import com.nikhil.mail_service.model.EmailPayload;
import com.nikhil.mail_service.model.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@RequiredArgsConstructor
@Service
public class EmailOrchestrator {
    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;
    public void dailyMail(){
        UserDTO[] users=restTemplate.getForObject("http://user-service:8080/api/users/all",UserDTO[].class);
        Content content=restTemplate.getForObject("http://content-service:8081/api/content/next", Content.class);
        if(users!=null&&content!=null){
            for(UserDTO user:users){
                rabbitTemplate.convertAndSend("emailQueue",new EmailPayload(user.getEmail(), content));

            }
        }
    }
}
