package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.dto.UserRequestDto;
import com.api.client_feedback_hub.dto.UserResponseDto;
import com.api.client_feedback_hub.entity.User;
import com.api.client_feedback_hub.mapper.UserMapper;
import com.google.firebase.database.*;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class UserService {
    UserMapper userMapper;

    private static final String DEFAULT_ROLE = "USER";
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public CompletableFuture<List<UserResponseDto>> getAllUsers() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        List<UserResponseDto> userList = new ArrayList<>();
        CompletableFuture<List<UserResponseDto>> futureUsers = new CompletableFuture<>();

        logger.info("Starting to fetch all users from database");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("DataSnapshot received from database");
                if (dataSnapshot.exists()) {
                    logger.info("Data found, processing users...");
                    // Iterate through each child in the snapshot
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        User user = snapshot.getValue(User.class);
                        if (user != null) {
                            userList.add(userMapper.convertToDto(user));
                            logger.info("User added: {}", user.getUserId());
                        } else {
                            logger.warn("Failed to parse data as User object");
                        }
                    });
                    // Complete the future with the list of users
                    futureUsers.complete(userList);
                } else {
                    // Complete the future with no data
                    logger.info("No data found");
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

    public CompletableFuture<UserResponseDto> getUserById(Long id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<UserResponseDto> future = new CompletableFuture<>();

        // Check if the user exists in the database
        ref.child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // User exists, retrieve the user data
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        // Complete the future with the user data
                        future.complete(userMapper.convertToDto(user));
                    } else {
                        // Complete exceptionally if parsing fails
                        future.completeExceptionally(new RuntimeException("User data found but failed to parse it"));
                    }
                } else {
                    // User does not exist, complete future exceptionally
                    logger.warn("No user found with ID: {}", id);
                    future.completeExceptionally(new RuntimeException("No user found with the provided ID: " + id));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error occurred while retrieving user with ID {}: {}", id, databaseError.getMessage());
                // Complete exceptionally on cancellation
                future.completeExceptionally(new RuntimeException("Error occurred while accessing the database for user ID: " + id));
            }
        });

        return future;
    }

    public CompletableFuture<String> createUser(UserRequestDto userRegisterDto) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference counterRef = FirebaseDatabase.getInstance().getReference("userIdCounter");
        CompletableFuture<String> future = new CompletableFuture<>();

        // Retrieve the current user ID from the counter
        counterRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Long currentId = dataSnapshot.getValue(Long.class);
                if (currentId == null) {
                    currentId = 0L;
                }
                Long uniqueUserId = currentId + 1; // Increment the ID

                // Hash the password
                String hashedPassword = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt());

                // Create a new user
                User newUser = new User(uniqueUserId, userRegisterDto.getEmail(), userRegisterDto.getName(),
                        userRegisterDto.getPhoneNumber(), DEFAULT_ROLE, hashedPassword, 0.0, 0);

                // Save the user in the database
                ref.child(String.valueOf(uniqueUserId)).setValue(newUser, (databaseError, databaseReference) -> {
                    if (databaseError != null) {
                        logger.error("Failed to save user: {}", databaseError.getMessage());
                        // Complete the future exceptionally to signal the error
                        future.completeExceptionally(new RuntimeException("User creation failed: " + databaseError.getMessage()));
                    } else {
                        logger.info("User saved successfully");
                        // Update the counter with the new ID
                        counterRef.setValue(uniqueUserId, (databaseError1, databaseReference1) -> {
                            if (databaseError1 != null) {
                                logger.error("Failed to update user ID counter: {}", databaseError1.getMessage());
                            }
                        });
                        // Complete the future with a success response
                        future.complete("User created with ID: " + uniqueUserId);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Complete the future exceptionally if the read operation is cancelled
                future.completeExceptionally(new RuntimeException("Failed to read user ID counter: " + databaseError.getMessage()));
            }
        });
        return future;
    }

    public CompletableFuture<String> updateUser(Long id, UserRequestDto userRequestDto) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");
        CompletableFuture<String> future = new CompletableFuture<>();

        // Check if the user exists in the database
        ref.child(String.valueOf(id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String hashedPassword = BCrypt.hashpw(userRequestDto.getPassword(), BCrypt.gensalt());
                    // User exists, update the existing user data
                    User user = dataSnapshot.getValue(User.class);
                    user.setEmail(userRequestDto.getEmail());
                    user.setName(userRequestDto.getName());
                    user.setPhoneNumber(userRequestDto.getPhoneNumber());
                    user.setHashedPassword(hashedPassword);
                    ref.child(String.valueOf(id)).setValue(user, (databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            logger.error("Failed to update user: {}", databaseError.getMessage());
                            // Complete the future exceptionally to signal the error
                            future.completeExceptionally(new RuntimeException("User update failed: " + databaseError.getMessage()));
                        } else {
                            logger.info("User updated successfully");
                            // Complete the future with a success response
                            future.complete(String.valueOf(id));
                        }
                    });
                } else {
                    // User does not exist, complete future exceptionally
                    logger.warn("User with ID: {} does not exist", id);
                    future.completeExceptionally(new RuntimeException("User not found: " + id));
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

    public CompletableFuture<Void> deleteUser(Long id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(String.valueOf(id));
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Check if the user exists in the database
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Deleting a specific user by its ID
                    ref.removeValue((databaseError, databaseReference) -> {
                        if (databaseError != null) {
                            logger.error("Failed to delete user with ID: {}. Error: {}", id, databaseError.getMessage());
                            // Rounding out the future with an exception
                            future.completeExceptionally(new RuntimeException("Failed to delete user with ID: " + id + ". Error: " + databaseError.getMessage()));
                        } else {
                            logger.info("User with ID: {} was deleted successfully.", id);
                            // Ending future successfully
                            future.complete(null);
                        }
                    });
                } else {
                    logger.warn("User with ID: {} does not exist.", id);
                    // End the future with an exception if the user is not found
                    future.completeExceptionally(new RuntimeException("User with ID: " + id + " does not exist."));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Error checking for user with ID: {}. Error: {}", id, databaseError.getMessage());
                // End future with an exception if the operation was canceled
                future.completeExceptionally(new RuntimeException("Error checking for user with ID: " + id + ". Error: " + databaseError.getMessage()));
            }
        });

        return future;
    }
}