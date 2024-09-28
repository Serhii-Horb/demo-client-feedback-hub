package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.entity.User;
import com.api.client_feedback_hub.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/firebase")
public class FirebaseController {

    private final FirebaseService firebaseService;


    public FirebaseController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User createdUser = userService.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updatedUser = userService.update(id, user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

//    @PostMapping("/realtime-database")
//    public ResponseEntity<String> addRealtimeData(@RequestParam String key, @RequestParam String value) {
//        firebaseService.addData(key, value);
//        return ResponseEntity.ok("Data added to Realtime Database successfully");
//    }
//
//    @PostMapping("/firestore")
//    public ResponseEntity<String> addFirestoreData(@RequestParam String userId, @RequestParam String name, @RequestParam String email) throws ExecutionException, InterruptedException {
//        firestoreService.addUser(userId, name, email);
//        return ResponseEntity.ok("User added to Firestore successfully");
//    }
}
