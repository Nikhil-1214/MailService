package com.nikhil.mail_service;

import com.nikhil.mail_service.model.ContentDTO;
import com.nikhil.mail_service.model.EmailPayload;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
public class EmailListener {
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final RestTemplate restTemplate; // Use this to talk to content-service

    @RabbitListener(queues="emailQueue")
    public void sendmail(UserRegistrationEvent event) throws MessagingException {
        // 1. FETCH CONTENT (The Orchestrator step)
        String contentUrl = "http://content-service:8081/api/content/" + event.getContentId();
        ContentDTO content = restTemplate.getForObject(contentUrl, ContentDTO.class);

        // 2. BUILD EMAIL
        if (content == null) {
            System.err.println("CRITICAL: Content service returned null for ID: " + event.getContentId());
            return; // Exit so we don't crash
        }
        Context context = new Context();
        context.setVariable("Concept", content.getConcept());
        context.setVariable("Theory", content.getTheory());

        String htmlContent = templateEngine.process("Daily-DSA-email", context);

        // 3. SEND
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(event.getUserEmail());
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}