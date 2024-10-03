package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.entity.User;
import com.api.client_feedback_hub.entity.enums.Role;
import com.api.client_feedback_hub.service.FirebaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "User controller", description = "Getting, deleting, or updating a user profile is done using this controller.")
@RequiredArgsConstructor // Убирает необходимость в ручном создании конструктора
public class FirebaseController {

    private final FirebaseService firebaseService; // Обязательно проверьте, что этот сервис правильно реализован.

    @GetMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Finds the user.",
            description = "Allows you to find one of the registered users by using the id."
    )
    public ResponseEntity<CompletableFuture<User>> getUserById(@PathVariable Long userId) {
        CompletableFuture<User> user = firebaseService.findById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build(); // Возврат 404, если пользователь не найден
        }
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Creates a new user.",
            description = "Allows you to create a new user."
    )
    public ResponseEntity<String> createUser(@RequestBody User user) {
        try {
            firebaseService.addUser(user.getEmail(), user.getName(), user.getPhoneNumber(), user.getRole(), user.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            System.err.println("Error creating user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

//    @PutMapping("/{userId}")
//    @Operation(
//            summary = "Updates an existing user.",
//            description = "Allows you to update user data by id."
//    )
//    public ResponseEntity<CompletableFuture<User>> updateUser(@PathVariable Long userId, @RequestBody User user) {
//        CompletableFuture<User> updatedUser = firebaseService.updateUser(userId, user);
//        if (updatedUser == null) {
//            return ResponseEntity.notFound().build(); // Возврат 404, если пользователь не найден для обновления
//        }
//        return ResponseEntity.ok(updatedUser);
//    }

//    @PutMapping(value = "/{userId}")
//    @ResponseStatus(HttpStatus.OK)
//    @Operation(
//            summary = "User update.",
//            description = "Allows the user to update their data."
//    )
//    public ResponseEntity<User> updateUser(
//            @PathVariable Long userId,
//            @RequestBody User updatedUser
//    ) {
//        CompletableFuture<User> futureUser = new CompletableFuture<>();
//
//        // Обновление данных пользователя через сервис
//        firebaseDatabaseService.getUser(userId, existingUser -> {
//            if (existingUser != null) {
//                // Обновляем данные пользователя
//                existingUser.setEmail(updatedUser.getEmail());
//                existingUser.setName(updatedUser.getName());
//                existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
//                existingUser.setRole(updatedUser.getRole());
//
//                // Сохранение обновленного пользователя в базе данных
//                firebaseDatabaseService.updateUser(userId, existingUser);
//
//                futureUser.complete(existingUser);
//            } else {
//                futureUser.completeExceptionally(new RuntimeException("User not found"));
//            }
//        });

        // Ждем завершения CompletableFuture и возвращаем обновленного пользователя
//        try {
//            User user = futureUser.get();
//            return ResponseEntity.ok(user); // Возвращаем обновленного пользователя
//        } catch (InterruptedException | ExecutionException e) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Возвращаем 404, если ошибка
//        }
//    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deletes the user.", description = "Allows you to delete a user profile.")
    public ResponseEntity<String> deleteUserProfileById(@PathVariable @Valid @Min(1) Long userId) {
        // Асинхронно удаляем пользователя и обрабатываем результат
        CompletableFuture<Boolean> deletionResult = firebaseService.deleteUser(userId);

        // Ожидаем результат и возвращаем соответствующий HTTP статус
        return deletionResult.thenApply(deleted -> {
            if (deleted) {
                return ResponseEntity.ok("User deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user.");
            }
        }).exceptionally(e -> {
            System.err.println("Error deleting user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting user: " + e.getMessage());
        }).join();
    }
}