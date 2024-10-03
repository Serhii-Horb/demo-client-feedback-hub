package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.entity.User;
import com.api.client_feedback_hub.entity.enums.Role;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseService {
    private final DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("users");
    }

//    @PostConstruct
//    public void checkConnectionWithRead() {
//        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    System.out.println("Data read successfully: " + dataSnapshot.getValue());
//                } else {
//                    System.out.println("No data found at test_connection.");
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.err.println("Failed to read data: " + databaseError.getMessage());
//            }
//        });
//    }

    public CompletableFuture<User> findById(Long userId) {
        CompletableFuture<User> futureUser = new CompletableFuture<>();

        databaseReference.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        System.out.println("User found: " + user);
                        futureUser.complete(user); // Завершаем CompletableFuture с найденным пользователем
                    } else {
                        System.err.println("Failed to deserialize user from Firebase for ID: " + userId);
                        futureUser.complete(null); // Десериализация не удалась
                    }
                } else {
                    System.out.println("No user found with ID: " + userId);
                    futureUser.complete(null); // Пользователь не найден
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Database error: " + databaseError.getMessage());
                futureUser.completeExceptionally(databaseError.toException()); // Завершаем с ошибкой
            }
        });

        return futureUser; // Возвращаем CompletableFuture
    }

    public void addUser(String email, String name, String phoneNumber,
                        Role role, Long userId) {
        User user = new User(email, name, phoneNumber, role, userId);
        databaseReference.child(String.valueOf(userId)).setValueAsync(user);
    }

//    public CompletableFuture<User> updateUser(Long id, User user) {
//        CompletableFuture<User> futureUser = new CompletableFuture<>();
//
//        databaseReference.child(String.valueOf(id)).setValue(user)
//                .addOnSuccessListener(aVoid -> futureUser.complete(user)) // Завершаем CompletableFuture с обновленным пользователем
//                .addOnFailureListener(e -> futureUser.completeExceptionally(e)); // Завершаем с ошибкой
//
//        return futureUser; // Возвращаем CompletableFuture
//    }

    public CompletableFuture<Boolean> deleteUser(Long userId) {
        DatabaseReference userRef = databaseReference.child(String.valueOf(userId));
        System.out.println("Attempting to delete user at path: " + userRef.toString());

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        userRef.removeValue((error, ref) -> {
            if (error != null) {
                System.err.println("Failed to delete user: " + error.getMessage());
                future.complete(false); // Удаление не удалось
            } else {
                System.out.println("User deleted successfully.");
                future.complete(true); // Удаление успешно
            }
        });

        return future;
    }


}
