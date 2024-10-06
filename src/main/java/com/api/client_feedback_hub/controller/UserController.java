package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.dto.UserRequestDto;
import com.api.client_feedback_hub.dto.UserResponseDto;
import com.api.client_feedback_hub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id).thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createUser(@RequestBody UserRequestDto userRegisterDto) {
        return userService.createUser(userRegisterDto)
                .thenApply(userId -> ResponseEntity.ok("User created successfully with ID: " + userId));
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userRegisterDto) {
        return userService.updateUser(id, userRegisterDto)
                .thenApply(userId -> ResponseEntity.ok("User updated successfully with ID: " + userId));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<UserResponseDto>>> getAllUsers() {
        return userService.getAllUsers()
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .thenApply(result -> ResponseEntity.ok("User deletion requested for ID: " + id));
    }
}