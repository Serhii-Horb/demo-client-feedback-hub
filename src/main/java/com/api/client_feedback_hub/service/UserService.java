package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.mapper.UserRegisterDto;
import com.api.client_feedback_hub.model.User;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public CompletableFuture<List<User>> getAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        List<User> userList = new ArrayList<>();
        CompletableFuture<List<User>> futureUsers = new CompletableFuture<>();

        logger.info("Starting to fetch all users from Firebase Database");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("DataSnapshot received from Firebase");
                if (dataSnapshot.exists()) {
                    logger.info("Data found, processing users...");
                    // Iterate through each child in the snapshot
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userList.add(user);
                            logger.info("User added: {}", user.getUserId());
                        } else {
                            logger.warn("Failed to parse data as User object");
                        }
                    });
                    // Complete the future with the list of users
                    logger.info("No data found, returning an empty list");
                    futureUsers.complete(userList);
                } else {
                    // Complete the future with no data
                    logger.info("No data found, returning an empty list");
                    futureUsers.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error: {}", databaseError.getMessage());
                futureUsers.completeExceptionally(new RuntimeException("Database read failed"));
            }
        });
        return futureUsers;
    }

    public CompletableFuture<User> getUserById(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<User> future = new CompletableFuture<>();

        // Check if the user exists in the database
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, retrieve the user data
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Complete the future with the user data
                        future.complete(user);
                    } else {
                        // Complete exceptionally if parsing fails
                        future.completeExceptionally(new RuntimeException("Failed to parse user data"));
                    }
                } else {
                    // User does not exist, complete future exceptionally
                    logger.warn("User with ID: {} does not exist", id);
                    future.completeExceptionally(new RuntimeException("User not found: " + id));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Failed to check user existence: {}", databaseError.getMessage());
                // Complete exceptionally on cancellation
                future.completeExceptionally(new RuntimeException("Failed to check user existence"));
            }
        });

        return future;
    }


    public CompletableFuture<String> createUser(User user) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<String> future = new CompletableFuture<>();
        ref.child(user.getUserId()).setValue(user, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    logger.error("Failed to save user: {}", databaseError.getMessage());
                    // Complete the future exceptionally to signal the error
                    future.completeExceptionally(new RuntimeException("User creation failed: " + databaseError.getMessage()));
                } else {
                    logger.info("User saved successfully");
                    // Complete the future with a success response
                    future.complete("User created with ID: " + user.getUserId());
                }
            }
        });
        return future;
    }

    public CompletableFuture<String> updateUser(String id, UserRegisterDto userRegisterDto) {

        User user = new User(id, userRegisterDto.getEmail(), userRegisterDto.getName(), userRegisterDto.getPhoneNumber(), DEFAULT_ROLE, hashedPassword);
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<String> future = new CompletableFuture<>();
        // Check if the user exists in the database
        ref.child(user.getUserId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, update the existing user data
                    ref.child(user.getUserId()).setValue(user, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                logger.error("Failed to update user: {}", databaseError.getMessage());
                                // Complete the future exceptionally to signal the error
                                future.completeExceptionally(new RuntimeException("User update failed: " + databaseError.getMessage()));
                            } else {
                                logger.info("User updated successfully");
                                // Complete the future with a success response
                                future.complete(user.getUserId());
                            }
                        }
                    });
                } else {
                    // User does not exist, complete future exceptionally
                    logger.warn("User with ID: {} does not exist", user.getUserId());
                    future.completeExceptionally(new RuntimeException("User not found: " + user.getUserId()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Failed to check user existence: {}", databaseError.getMessage());
                future.completeExceptionally(new RuntimeException("Failed to check user existence"));
            }
        });

        return future;
    }

    public CompletableFuture<Void> deleteUser(String userId) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
        CompletableFuture<Void> future = new CompletableFuture<>();
        ref.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    logger.error("Failed to delete user with ID: {}. Error: {}", userId, databaseError.getMessage());
                    // Complete the future exceptionally
                    future.completeExceptionally(new RuntimeException("Failed to delete user with ID: " + userId + ". Error: " + databaseError.getMessage()));
                } else {
                    logger.info("User with ID: {} was deleted successfully.", userId);
                    // Complete the future normally
                    future.complete(null);
                }
            }
        });
        return future;
    }
}