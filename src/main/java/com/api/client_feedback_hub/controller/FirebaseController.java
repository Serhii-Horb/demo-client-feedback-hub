package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.entity.User;
import com.api.client_feedback_hub.service.FirebaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    @Operation(
//            summary = "Creates a new user.",
//            description = "Allows you to create a new user."
//    )
//    public ResponseEntity<CompletableFuture<User>> createUser(@RequestBody User user) {
//        CompletableFuture<User> createdUser = firebaseService.saveUser(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
//    }

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

    @DeleteMapping("/{userId}")
    @Operation(
            summary = "Deletes a user.",
            description = "Allows you to delete a user by id."
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        // Асинхронное выполнение операции удаления пользователя
        CompletableFuture<Boolean> isDeletedFuture = firebaseService.deleteUser(userId);

        // Ожидание завершения операции и обработка результата
        try {
            boolean isDeleted = isDeletedFuture.get(); // Получаем результат удаления

            if (!isDeleted) {
                return ResponseEntity.notFound().build(); // Возврат 404, если пользователь не найден для удаления
            }
            return ResponseEntity.noContent().build(); // Возвращает статус 204 No Content при успешном удалении
        } catch (InterruptedException | ExecutionException e) {
            // Обработка исключений
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}