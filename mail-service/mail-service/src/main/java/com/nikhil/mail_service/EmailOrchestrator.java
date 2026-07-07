package com.nikhil.mail_service;

import com.nikhil.mail_service.model.ContentDTO;
import com.nikhil.mail_service.model.EmailPayload;
import com.nikhil.mail_service.model.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailOrchestrator {

    private final RestTemplate restTemplate;
    private final RabbitTemplate rabbitTemplate;

    @Value("${services.user-service}")
    private String userServiceUrl;

    @Value("${services.content-service}")
    private String contentServiceUrl;

    @Value("${rabbitmq.queue.email}")
    private String newsletterQueue;

    @Value("${rabbitmq.exchange.newsletter:newsletter-exchange}")
    private String newsletterExchange;

    @Value("${rabbitmq.routing-key.newsletter:newsletter.send}")
    private String newsletterRoutingKey;

    // constructor removed - Lombok's @RequiredArgsConstructor will inject RestTemplate and RabbitTemplate

    @Scheduled(cron = "${newsletter.cron}")
    public void sendDailyMail() {
        log.info("=== Starting Daily DSA Email Job ===");

        UserDTO[] users;
        ContentDTO content;

        // 1. Safely fetch users (Make sure this endpoint filters for SUBSCRIBERS only)
        try {
            users = restTemplate.getForObject(userServiceUrl + "/api/users/all", UserDTO[].class);
        } catch (Exception e) {
            log.error("Failed to fetch users from user-service: {}", e.getMessage());
            return;
        }

        // 2. Safely fetch content
        try {
            content = restTemplate.getForObject(contentServiceUrl + "/api/content/next", ContentDTO.class);
        } catch (Exception e) {
            log.error("Failed to fetch today's content from content-service: {}", e.getMessage());
            return;
        }

        if (users == null || users.length == 0 || content == null) {
            log.warn("Job aborted: No subscribers found or content is missing.");
            return;
        }

        log.info("Sending daily DSA email to {} users. Topic: {}", users.length, content.getConcept());

        // 3. Publish to Queue with individual error handling
        int successCount = 0;
        for (UserDTO user : users) {
            try {
                EmailPayload payload = new EmailPayload(
                        user.getEmail(),
                        user.getName(),
                        content
                );
                rabbitTemplate.convertAndSend(newsletterExchange, newsletterRoutingKey, payload);
                successCount++;
            } catch (Exception e) {
                // If one message fails, log it and continue to the next user
                log.error("Failed to queue email for user {}: {}", user.getEmail(), e.getMessage());
            }
        }

        log.info("=== Job Complete: {}/{} emails queued successfully ===", successCount, users.length);
    }
}