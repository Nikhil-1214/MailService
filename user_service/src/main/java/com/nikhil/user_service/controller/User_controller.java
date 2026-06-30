package com.nikhil.user_service.controller;

import com.nikhil.user_service.UserDTO;
import com.nikhil.user_service.service.User_service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class User_controller {
    private final User_service userService;
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserDTO userDTO){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(userDTO));

    }
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUser(){
        return ResponseEntity.ok(userService.getAllUser());
    }


}
