package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.entity.Feedback;
import com.google.firebase.database.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedbackService {
    private final DatabaseReference databaseReference;

    public FeedbackService() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference("feedbacks");
    }

    public void addFeedback(String feedbackId, String reviewerId, int rating, String comment, Long createdAt) {
Feedback feedback = new Feedback(feedbackId, reviewerId, rating, comment, createdAt);
databaseReference.child(String.valueOf(feedbackId)).setValueAsync(feedback);
    }

    public CompletableFuture<Boolean> deleteFeedback(String feedbackId) {
        DatabaseReference feedbackRef = databaseReference.child(String.valueOf(feedbackId));
        System.out.println("Attempting to delete feedback at path: " + feedbackRef.toString());

        CompletableFuture<Boolean> future = new CompletableFuture<>();

        feedbackRef.removeValue((error, ref) -> {
            if (error != null) {
                System.err.println("Failed to delete feedback: " + error.getMessage());
                future.complete(false); // Удаление не удалось
            } else {
                System.out.println("Feedback deleted successfully.");
                future.complete(true); // Удаление успешно
            }
        });

        return future;
    }

    public CompletableFuture<Feedback> findFeedbackById(String feedbackId) {
        CompletableFuture<Feedback> futureFeedback = new CompletableFuture<>();

        databaseReference.child(String.valueOf(feedbackId)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        System.out.println("Feedback found: " + feedback);
                        futureFeedback.complete(feedback); // Завершаем CompletableFuture с найденным пользователем
                    } else {
                        System.err.println("Failed to deserialize feedback from Firebase for ID: " + feedbackId);
                        futureFeedback.complete(null); // Десериализация не удалась
                    }
                } else {
                    System.out.println("No feedback found with ID: " + feedbackId);
                    futureFeedback.complete(null); // Пользователь не найден
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Database error: " + databaseError.getMessage());
                futureFeedback.completeExceptionally(databaseError.toException()); // Завершаем с ошибкой
            }
        });

        return futureFeedback; // Возвращаем CompletableFuture
    }

    public void updateFeedback(String feedbackId, String reviewerId, int rating, String comment, Long updatedAt) {
        // Создаем объект Feedback
        Feedback updatedFeedback = new Feedback(feedbackId, reviewerId, rating, comment, updatedAt);

        // Преобразуем объект Feedback в карту для обновления данных
        Map<String, Object> feedbackUpdates = new HashMap<>();
        feedbackUpdates.put("feedbackId", updatedFeedback.getFeedbackId());
        feedbackUpdates.put("reviewerId", updatedFeedback.getReviewerId());
        feedbackUpdates.put("rating", updatedFeedback.getRating());
        feedbackUpdates.put("comment", updatedFeedback.getComment());
        feedbackUpdates.put("updatedAt", updatedFeedback.getCreatedAt());

        // Обновляем отзыв в Firebase
        databaseReference.child(feedbackId).updateChildrenAsync(feedbackUpdates);
    }

}
