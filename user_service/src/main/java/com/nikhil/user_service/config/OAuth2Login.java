package com.nikhil.user_service.config;

import com.nikhil.user_service.UserRegistrationEvent;
import com.nikhil.user_service.model.User;
import com.nikhil.user_service.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2Login implements AuthenticationSuccessHandler {

    private final UserService userService;
    private final RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.user:user-exchange}")
    private String userExchange;

    @Value("${rabbitmq.routing-key.user:user.registered}")
    private String userRoutingKey;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Get the verified user details from Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        log.info("OAuth2 Login successful for email: {}", email);

        // 2. Check if this is a brand new user
//        if (!userService.userExists(email)) {

            // Save to database
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            userService.saveOAuthUser(newUser);

            // 3. Fire the RabbitMQ Event for the Welcome Email!
            UserRegistrationEvent event = new UserRegistrationEvent(email, name);
            rabbitTemplate.convertAndSend(userExchange, userRoutingKey, event);
            log.info("New user registered via OAuth2. Welcome email queued.");

        // 4. Redirect the user back to your frontend/dashboard
        response.sendRedirect("http://localhost:3000/dashboard"); // Update with your frontend URL
    }
}