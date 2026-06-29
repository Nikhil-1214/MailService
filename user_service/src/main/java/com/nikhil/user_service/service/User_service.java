package com.nikhil.user_service.service;

import com.nikhil.user_service.UserDTO;
import com.nikhil.user_service.UserEvent;
import com.nikhil.user_service.UserRegistrationEvent;
import com.nikhil.user_service.model.User;
import com.nikhil.user_service.repository.User_repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class User_service {
    private final User_repository userRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitAdmin rabbitAdmin; // Inject this!

    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        // 1. Save User
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User savedUser = userRepository.save(user);

        // 2. IMPORTANT: Create the Event Object!
        // Make sure 'UserRegistrationEvent' class has a constructor or setters
        UserRegistrationEvent event = new UserRegistrationEvent();
        event.setUserEmail(savedUser.getEmail());
        event.setContentId("DSA-TOPIC-001");

        // 3. Send the OBJECT (Jackson will convert it to JSON)
        rabbitTemplate.convertAndSend("user-exchange", "user.registered", event);

        return new UserDTO(savedUser.getName(), savedUser.getEmail());
    }
}