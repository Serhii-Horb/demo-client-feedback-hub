package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.entity.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseService {
    private final DatabaseReference databaseReference;

    public FirebaseService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("users"); // Убедитесь, что путь соответствует вашей структуре БД
    }

    public CompletableFuture<User> findById(Long userId) {
        CompletableFuture<User> futureUser = new CompletableFuture<>();

        databaseReference.child(String.valueOf(userId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    futureUser.complete(user); // Завершаем CompletableFuture
                } else {
                    futureUser.complete(null); // Пользователь не найден
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                futureUser.completeExceptionally(databaseError.toException()); // Завершаем с ошибкой
            }
        });

        return futureUser; // Возвращаем CompletableFuture
    }

//    public CompletableFuture<User> saveUser(User user) {
//        CompletableFuture<User> futureUser = new CompletableFuture<>();
//
//        String userId = databaseReference.push().getKey(); // Генерируем уникальный ID для нового пользователя
//        user.setUserId(Long.parseLong(userId)); // Установите ID пользователя в объект User
//
//        databaseReference.child(userId).setValue(user)
//                .addOnSuccessListener(aVoid -> futureUser.complete(user)) // Завершаем CompletableFuture с созданным пользователем
//                .addOnFailureListener(e -> futureUser.completeExceptionally(e)); // Завершаем с ошибкой
//
//        return futureUser; // Возвращаем CompletableFuture
//    }

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

        // Создаем CompletableFuture для обработки результата удаления
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        userRef.removeValue(new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null) {
                    System.err.println("Failed to delete user: " + error.getMessage());
                    future.complete(false); // Удаление не удалось
                } else {
                    System.out.println("User deleted successfully.");
                    future.complete(true); // Удаление успешно
                }
            }
        });

        return future; // Возвращаем CompletableFuture
    }
}
