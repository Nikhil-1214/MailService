package com.nikhil.user_service.controller;

import com.nikhil.user_service.model.UserDTO;
import com.nikhil.user_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users") // <-- 1. Base path for all methods
@CrossOrigin(origins = "*")   // <-- 2. Allows your HTML file to communicate with Java without security blocks
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "Authentication Successful! Welcome to the Backend API.";
    }

    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUser() {
        return ResponseEntity.ok(userService.getAllUser());
    }

    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("User Service is UP and running!");
    }

    // 3. FIXED: Changed from "/api/users/unsubscribe" to just "/unsubscribe"
    @GetMapping("/unsubscribe")
    public ResponseEntity<String> unsubscribeUser(@RequestParam String email) {
        log.info("DEBUG: Attempting to unsubscribe user with email: '{}'", email);

        try {
            userService.unsubscribeUser(email);
            return ResponseEntity.ok("<h1>Unsubscribed</h1><p>You have been removed.</p>");
        } catch (Exception e) {
            log.error("DEBUG: Unsubscribe failed because: {}", e.getMessage());
            return ResponseEntity.status(404).body("<h1>Error</h1><p>User not found.</p>");
        }
    }

    // 4. This becomes /api/users/login (Perfect!)
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserDTO loginRequest) {
        // Check if the user exists in your database
        boolean exists = userService.userExists(loginRequest.getEmail());

        if (exists) {
            // Return 200 OK so the HTML JavaScript knows to show the success message
            return ResponseEntity.ok("Login Successful");
        } else {
            // Return 401 Unauthorized so the HTML JavaScript knows to show an error
            return ResponseEntity.status(401).body("User not found");
        }
    }
}