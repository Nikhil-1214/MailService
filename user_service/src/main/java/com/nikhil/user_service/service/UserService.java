package com.nikhil.user_service.service;

import com.nikhil.user_service.model.UserDTO;
import com.nikhil.user_service.UserRegistrationEvent;
import com.nikhil.user_service.model.User;
import com.nikhil.user_service.repository.User_repository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final User_repository userRepository;
    private final RabbitTemplate rabbitTemplate;
    public boolean userExists(String email) {
        return userRepository.existsByEmail(email);
    }
    public void saveOAuthUser(User user){
        userRepository.save(user);
    }

    public List<UserDTO> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if(userExists(userDTO.getEmail())){
            throw new RuntimeException("User with email " + userDTO.getEmail() + " already exists.");
        }
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        User savedUser = userRepository.save(user);

        // 2. IMPORTANT: Create the Event Object!
        // Make sure 'UserRegistrationEvent' class has a constructor or setters
        UserRegistrationEvent event = new UserRegistrationEvent();
        event.setUserEmail(savedUser.getEmail());
        event.setUserName(savedUser.getName());

        rabbitTemplate.convertAndSend("user-exchange", "user.registered", event);

        return new UserDTO(savedUser.getName(), savedUser.getEmail());
    }
    public void unsubscribeUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Completely removes the user from the database
        userRepository.delete(user);
        log.info("User {} has been successfully deleted/unsubscribed.", email);
    }
}