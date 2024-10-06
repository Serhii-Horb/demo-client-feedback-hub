package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.auth.AuthService;
import com.api.client_feedback_hub.dto.LoginDto;
import com.api.client_feedback_hub.dto.UserRequestDto;
import com.api.client_feedback_hub.dto.UserResponseDto;
import com.api.client_feedback_hub.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;
    @Autowired
    private FirebaseAuth firebaseAuth;

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyC_bLKYSJjjXd2YigxHxm_DcL4iCxxbx2s";

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRequestDto userRequestDto) {
        try {
            // Attempt to register the user using the provided UserRequestDto
            String userId = authService.registerUser(userRequestDto);
            // Return a response indicating the user was successfully registered
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered with UID: " + userId);
        } catch (FirebaseAuthException e) {
            // If registration fails, return an error response with the exception message
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginDto loginDto) {
        RestTemplate restTemplate = new RestTemplate(); // Create a new RestTemplate instance
        HttpHeaders headers = new HttpHeaders(); // Create headers for the HTTP request
        headers.setContentType(MediaType.APPLICATION_JSON); // Set the content type to JSON

        // Construct the request body for Firebase authentication
        String requestBody = String.format("{\"email\":\"%s\", \"password\":\"%s\", \"returnSecureToken\":true}",
                loginDto.getEmail(), loginDto.getPassword());

        // Create an HttpEntity with the request body and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // Perform a POST request to the Firebase authentication URL
        ResponseEntity<String> response = restTemplate.exchange(FIREBASE_AUTH_URL, HttpMethod.POST, entity, String.class);

        // Return the response from the Firebase authentication request
        return response;
    }

    @PostMapping(path = "/user-claims/{uid}")
    public void updateAuthority(@PathVariable String uid, @RequestBody String authorityToAdd)
            throws FirebaseAuthException {

        // Retrieve the current custom claims for the user identified by uid
        Map<String, Object> currentClaims = firebaseAuth.getUser(uid).getCustomClaims();

        // Create a new empty Set to hold the new roles and add the new authority
        Set<String> newRoles = new HashSet<>();
        newRoles.add(authorityToAdd);

        // Create a new map with updated custom claims
        Map<String, Object> newClaims = new HashMap<>(currentClaims);
        newClaims.put("authorities", new ArrayList<>(newRoles)); // Set only the new role

        // Update the user's custom claims with the new claims
        firebaseAuth.setCustomUserClaims(uid, newClaims);
    }

    @GetMapping("/user-claims/{uid}")
    public Map<String, Object> getUserClaims(@PathVariable String uid) throws FirebaseAuthException {
        // Retrieve the user record for the specified UID
        UserRecord userRecord = firebaseAuth.getUser(uid);

        // Get the custom claims associated with the user
        Map<String, Object> claims = userRecord.getCustomClaims();

        // Return the claims; if there are no claims, return an empty HashMap
        return claims != null ? claims : new HashMap<>();
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