//package com.api.client_feedback_hub.auth;
//import com.api.client_feedback_hub.mapper.LoginDto;
//import okhttp3.*;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class AuthController {
//
//    private static final String API_KEY = "YOUR_API_KEY"; // Замените на ваш API ключ
//    private static final String LOGIN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;
//
//    @PostMapping("/login")
//    public ResponseEntity<String> loginUser(@RequestBody LoginDto loginDto) {
//        OkHttpClient client = new OkHttpClient();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        SignInRequest requestPayload = new SignInRequest(loginDto.getEmail(), loginDto.getPassword());
//
//        try {
//            // Преобразуем объект в JSON
//            String json = objectMapper.writeValueAsString(requestPayload);
//            RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));
//
//            Request request = new Request.Builder()
//                    .url(LOGIN_URL)
//                    .post(requestBody)
//                    .build();
//
//            Response response = client.newCall(request).execute();
//
//            if (response.isSuccessful()) {
//                String responseBody = response.body().string();
//                // Возвращаем ответ с токеном или другой информацией о пользователе
//                return ResponseEntity.ok("User logged in successfully. Response: " + responseBody);
//            } else {
//                return ResponseEntity.status(401).body("Error logging in: " + response.message());
//            }
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
//        }
//    }
//
//    // Определите класс SignInRequest здесь или как отдельный класс
//}