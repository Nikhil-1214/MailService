package com.nikhil.mail_service;

import com.nikhil.mail_service.model.EmailPayload;
import com.nikhil.mail_service.model.UserRegistrationEvent;
import com.rabbitmq.client.Channel;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailListener {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // ── Flow 1: welcome email triggered by user registration ──────────────────

    @RabbitListener(
            queues = "${rabbitmq.queue.email:emailQueue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void sendWelcomeMail(
            UserRegistrationEvent event,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("Attempting to send welcome email to {}", event.getUserEmail());

        try {
            Context context = new Context();
            context.setVariable("userName",  event.getUserName());
            context.setVariable("userEmail", event.getUserEmail());

            sendHtml(event.getUserEmail(), "Welcome to DSA Daily! 🚀", "welcome-email", context);

            // SUCCESS: We sent the email! Tell RabbitMQ to delete the message from the queue.
            channel.basicAck(deliveryTag, false);
            log.info("Welcome email sent successfully to {}", event.getUserEmail());

        } catch (Exception e) {
            // FAILURE: Something went wrong (e.g., bad email address, SMTP server down).
            log.error("Failed to send welcome email to {}: {}", event.getUserEmail(), e.getMessage());

            // Tell RabbitMQ we failed.
            // (false, false = "Don't acknowledge multiple messages at once", and "Don't put it back in the queue, discard it")
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // ── Flow 2: daily newsletter triggered by EmailOrchestrator ───────────────

    @RabbitListener(
            queues = "${rabbitmq.queue.newsletter:newsletterQueue}",
            containerFactory = "rabbitListenerContainerFactory"
    )
    public void sendNewsletterMail(
            EmailPayload payload,
            Channel channel,
            @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) throws IOException {

        log.info("Attempting to send newsletter to {} — concept: {}",
                payload.getUserEmail(), payload.getContent().getConcept());

        try {
            Context context = new Context();
            context.setVariable("userName", payload.getUserName());
            // ADD THIS LINE SO THE UNSUBSCRIBE LINK WORKS
            context.setVariable("userEmail", payload.getUserEmail());
            context.setVariable("conceptNo",  payload.getContent().getId());
            context.setVariable("concept",  payload.getContent().getConcept());
            context.setVariable("theory",   payload.getContent().getTheory());
            context.setVariable("code",     payload.getContent().getCode());
            context.setVariable("example",  payload.getContent().getExample());

            sendHtml(payload.getUserEmail(), "📚 Your Daily DSA Concept", "Daily-DSA-email", context);

            // SUCCESS: Acknowledge the message
            channel.basicAck(deliveryTag, false);
            log.info("Newsletter sent successfully to {}", payload.getUserEmail());

        } catch (Exception e) {
            // FAILURE: Discard the message
            log.error("Failed to send newsletter to {}: {}", payload.getUserEmail(), e.getMessage());
            channel.basicNack(deliveryTag, false, false);
        }
    }

    // ── Shared Helper ─────────────────────────────────────────────────────────

    private void sendHtml(String to, String subject, String template, Context context)
            throws MessagingException {
        String html = templateEngine.process(template, context);
        MimeMessage message = mailSender.createMimeMessage();

        // true = indicates this is a multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // ADD THIS LINE
        helper.setFrom("beastmode1642@gmail.com");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }
}