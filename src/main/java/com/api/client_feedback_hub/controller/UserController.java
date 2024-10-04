package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.dto.UserRequestDto;
import com.api.client_feedback_hub.dto.UserResponseDto;
import com.api.client_feedback_hub.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);


//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody UserRegisterDto userRegisterDto) {
//        try {
//            // 1. Создание запроса для создания пользователя в Firebase Authentication
//            CreateRequest request = new CreateRequest()
//                    .setEmail(userRegisterDto.getEmail())
//                    .setPassword(userRegisterDto.getPassword())
//                    .setDisplayName(userRegisterDto.getName())
//                    .setPhoneNumber(userRegisterDto.getPhoneNumber())
//                    .setDisabled(false);
//
//            // 2. Создаем пользователя в Firebase Authentication
//            UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);
//
//
//            // 3. Сохраняем дополнительные данные в Realtime Database
//            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
//
//            String hashedPassword = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());
//            // Предположим, что по умолчанию всем новым пользователям назначается роль "user
//            User newUser = new User(userRecord.getUid(), userRegisterDto.getEmail(), userRegisterDto.getName(), userRegisterDto.getPhoneNumber(), DEFAULT_ROLE, hashedPassword);
//            // Сохраняем пользователя в базе данных
//            ref.child(userRecord.getUid()).setValueAsync(newUser);
//
//            // 4. Возвращаем успешный ответ
//            return ResponseEntity.ok("User registered successfully with ID: " + userRecord.getUid());
//
//        } catch (Exception e) {
//            // Обрабатываем ошибки и возвращаем соответствующее сообщение
//            return ResponseEntity.status(500).body("Error registering user: " + e.getMessage());
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
//        try {
//
//            // Проверяем учетные данные
//            SignInResult signInResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(loginDto.getEmail(), loginDto.getPassword());
//
//            // Получаем ID токен
//            String idToken = signInResult.getUser().getIdToken(true).getResult().getToken();
//
//            // Возвращаем успешный ответ с ID токеном
//            return ResponseEntity.ok("User logged in successfully. ID Token: " + idToken);
//        } catch (Exception e) {
//            // Обрабатываем ошибки и возвращаем соответствующее сообщение
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error logging in: " + e.getMessage());
//        }
//    }

//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
//
//
//        try {
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("role", "user");
//
//            // Получаем пользовательский токен
//            String idToken = FirebaseAuth.getInstance()
//                    .createCustomToken(loginDto.getEmail(), claims );
//            return ResponseEntity.ok("User logged in successfully. ID Token: " + idToken);
//        } catch (Exception e) {
//            return ResponseEntity.status(401).body("Error logging in: " + e.getMessage());
//        }
//    }

    //
//    @GetMapping("/profile")
//    public ResponseEntity<String> getUserProfile(@RequestHeader("Authorization") String token) throws Exception {
//        String idToken = token.substring(7); // удаляем "Bearer "
//        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
//
//        String uid = decodedToken.getUid(); // Идентификатор пользователя
//        // Логика для получения информации о пользователе из Firebase Realtime Database
//        return ResponseEntity.ok("User profile for UID: " + uid);
//    }
//
    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<UserResponseDto>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .thenApply(user -> ResponseEntity.ok(user))
                .exceptionally(ex -> {
                    logger.error("Error retrieving user: {}", ex.getMessage());
                    // Check if the error message indicates the user was not found
                    if (ex.getMessage().contains("No user found with the provided ID")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
                    }
                });
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createUser(@RequestBody UserRequestDto userRegisterDto) {
        return userService.createUser(userRegisterDto)
                .thenApply(userId -> ResponseEntity.ok("User created successfully with ID: " + userId))
                .exceptionally(ex -> {
                    // Log the error
                    logger.error("Error creating user: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create user: " + ex.getMessage());
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> updateUser(@PathVariable Long id, @RequestBody UserRequestDto userRegisterDto) {
        return userService.updateUser(id, userRegisterDto)
                .thenApply(userId -> ResponseEntity.ok("User updated successfully with ID: " + userId))
                .exceptionally(ex -> {
                    // Log the error
                    logger.error("Error updating user: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to update user: " + ex.getMessage());
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<UserResponseDto>>> getAllUsers() {
        return userService.getAllUsers()
                .thenApply(users -> ResponseEntity.ok(users))
                .exceptionally(e -> {
                    logger.error("Failed to fetch users: {}", e.getMessage());
                    // Handle errors
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id)
                .thenApply(result -> ResponseEntity.ok("User deletion requested for ID: " + id))
                .exceptionally(e -> {
                    logger.error("Error deleting user with ID: {}. Error: {}", id, e.getMessage());
                    // Handle errors
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error deleting user with ID: " + id);
                });
    }
}