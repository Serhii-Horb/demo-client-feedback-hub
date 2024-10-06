package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.dto.FeedbackRequestDto;
import com.api.client_feedback_hub.dto.FeedbackResponseDto;
import com.api.client_feedback_hub.entity.Feedback;
import com.api.client_feedback_hub.mapper.FeedbackMapper;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedbackService {
    final
    FeedbackMapper feedbackMapper;
    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    public FeedbackService(FeedbackMapper feedbackMapper) {
        this.feedbackMapper = feedbackMapper;
    }

    private void logDatabaseError(DatabaseError databaseError, String context) {
        logger.error("Database error during {}: {}", context, databaseError.getMessage());
    }

    private void logNoDataFound(String context) {
        logger.info("No data found, returning an empty list. Context: {}", context);
    }

    private void logFeedbackAdded(String feedbackId) {
        logger.info("Feedback added: {}.", feedbackId);
    }

    public CompletableFuture<List<FeedbackResponseDto>> getAllFeedbacks() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        List<FeedbackResponseDto> feedbackList = new ArrayList<>();
        CompletableFuture<List<FeedbackResponseDto>> futureFeedbacks = new CompletableFuture<>();

        logger.info("Starting to fetch all feedbacks from the database");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logFeedbackAdded(feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object...");
                        }
                    });
                    futureFeedbacks.complete(feedbackList);
                } else {
                    logNoDataFound("getAllFeedbacks");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "getAllFeedbacks");
                futureFeedbacks.completeExceptionally(new RuntimeException());
            }
        });
        return futureFeedbacks;
    }

    public CompletableFuture<FeedbackResponseDto> getFeedbackById(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        CompletableFuture<FeedbackResponseDto> future = new CompletableFuture<>();

        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        future.complete(feedbackMapper.convertToDto(feedback));
                    } else {
                        future.completeExceptionally(new RuntimeException("Failed to parse feedback data"));
                    }
                } else {
                    logger.warn("No feedback found with ID: {}", id);
                    future.completeExceptionally(new RuntimeException("No feedback found with the provided ID: " + id));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "getFeedbackById");
                future.completeExceptionally(new RuntimeException("Error occurred while accessing the database for feedback ID: " + id));
            }
        });
        return future;
    }

    public CompletableFuture<List<FeedbackResponseDto>> getAllFeedbacksByReviewerId(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        List<FeedbackResponseDto> feedbackList = new ArrayList<>();
        CompletableFuture<List<FeedbackResponseDto>> futureFeedbacks = new CompletableFuture<>();

        logger.info("Starting to fetch all feedbacks by reviewer with ID: {}", id);
        ref.orderByChild("reviewerId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logFeedbackAdded(feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object.");
                        }
                    });
                    futureFeedbacks.complete(feedbackList);
                } else {
                    logNoDataFound("getAllFeedbacksByReviewerId");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "getAllFeedbacksByReviewerId");
                futureFeedbacks.completeExceptionally(new RuntimeException("Database read failed"));
            }
        });
        return futureFeedbacks;
    }

    public CompletableFuture<List<FeedbackResponseDto>> getAllFeedbacksByRecipientId(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        List<FeedbackResponseDto> feedbackList = new ArrayList<>();
        CompletableFuture<List<FeedbackResponseDto>> futureFeedbacks = new CompletableFuture<>();

        logger.info("Starting to fetch all feedbacks by recipient with ID: {}", id);
        ref.orderByChild("recipientId").equalTo(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logger.info("Feedback added: {}", feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object");
                        }
                    });
                    futureFeedbacks.complete(feedbackList);
                } else {
                    logNoDataFound("getAllFeedbacksByRecipientId");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "getAllFeedbacksByRecipientId");
                futureFeedbacks.completeExceptionally(new RuntimeException("Database read failed"));
            }
        });
        return futureFeedbacks;
    }

    public CompletableFuture<String> createFeedback(FeedbackRequestDto feedbackRequestDto) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
        CompletableFuture<String> future = new CompletableFuture<>();

        checkUserExists(usersRef, feedbackRequestDto.getReviewerId(), "Reviewer")
                .thenCompose(reviewerExists -> checkUserExists(usersRef, feedbackRequestDto.getRecipientId(), "Recipient"))
                .thenAccept(recipientExists -> {
                    String uniqueFeedbackId = feedbackRef.push().getKey();
                    Feedback newFeedback = new Feedback(uniqueFeedbackId, feedbackRequestDto.getReviewerId(),
                            feedbackRequestDto.getRecipientId(), feedbackRequestDto.getFeedbackText(),
                            feedbackRequestDto.getGrade(), System.currentTimeMillis());

                    updateRecipientData(usersRef, feedbackRequestDto.getRecipientId(), feedbackRequestDto.getGrade())
                            .thenAccept(updated -> saveFeedback(feedbackRef, uniqueFeedbackId, newFeedback, future))
                            .exceptionally(e -> {
                                future.completeExceptionally(e);
                                return null;
                            });
                }).exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }

    private CompletableFuture<Boolean> checkUserExists(DatabaseReference usersRef, String userId, String userType) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        usersRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    future.complete(true);
                } else {
                    future.completeExceptionally(new RuntimeException(userType + " ID does not exist: " + userId));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "checkUserExists");
                future.completeExceptionally(new RuntimeException("Failed to check " + userType + " ID: " + userId));
            }
        });
        return future;
    }

    private CompletableFuture<Void> updateRecipientData(DatabaseReference usersRef, String recipientId, double grade) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        usersRef.child(recipientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer currentNumberReviewers = snapshot.child("numberReviewers").getValue(Integer.class);
                Double averageRating = snapshot.child("averageRating").getValue(Double.class);

                currentNumberReviewers++;
                averageRating = Math.round((averageRating * (currentNumberReviewers - 1) + grade) / currentNumberReviewers * 100.0) / 100.0;

                Map<String, Object> updates = new HashMap<>();
                updates.put("numberReviewers", currentNumberReviewers);
                updates.put("averageRating", averageRating);

                usersRef.child(recipientId).updateChildren(updates, (error, ref) -> {
                    if (error != null) {
                        logDatabaseError(error, "updateRecipientData");
                        future.completeExceptionally(new RuntimeException("Failed to update recipient data"));
                    } else {
                        future.complete(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "updateRecipientData");
                future.completeExceptionally(new RuntimeException("Failed to retrieve recipient data"));
            }
        });
        return future;
    }

    private void saveFeedback(DatabaseReference feedbackRef, String feedbackId, Feedback feedback, CompletableFuture<String> future) {
        feedbackRef.child(feedbackId).setValue(feedback, (error, ref) -> {
            if (error != null) {
                logDatabaseError(error, "saveFeedback");
                future.completeExceptionally(new RuntimeException("Feedback creation failed"));
            } else {
                logger.info("Feedback successfully saved with ID: {}", feedbackId);
                future.complete("Feedback created with ID: " + feedbackId);
            }
        });
    }

    public CompletableFuture<Void> deleteFeedbackById(String feedbackId) {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks").child(feedbackId);
        CompletableFuture<Void> future = new CompletableFuture<>();

        feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot feedbackSnapshot) {
                if (!feedbackSnapshot.exists()) {
                    logger.error("Feedback with ID: {} does not exist.", feedbackId);
                    future.completeExceptionally(new RuntimeException("Feedback with ID: " + feedbackId + " does not exist."));
                    return;
                }

                Feedback feedback = feedbackSnapshot.getValue(Feedback.class);
                if (feedback == null || feedback.getRecipientId() == null) {
                    future.completeExceptionally(new RuntimeException("Failed to parse feedback data or recipientId is missing."));
                    return;
                }

                updateRecipientDataOnDeletion(feedback.getRecipientId(), feedback.getGrade())
                        .thenAccept(updated -> deleteFeedback(feedbackRef, feedbackId, future))
                        .exceptionally(e -> {
                            future.completeExceptionally(e);
                            return null;
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "deleteFeedbackById");
                future.completeExceptionally(new RuntimeException("Failed to check feedback ID"));
            }
        });

        return future;
    }

    private void deleteFeedback(DatabaseReference feedbackRef, String feedbackId, CompletableFuture<Void> future) {
        feedbackRef.removeValue((error, ref) -> {
            if (error != null) {
                logDatabaseError(error, "deleteFeedback");
                future.completeExceptionally(new RuntimeException("Failed to delete feedback with ID: " + feedbackId));
            } else {
                logger.info("Feedback with ID: {} was deleted successfully.", feedbackId);
                future.complete(null);
            }
        });
    }

    private CompletableFuture<Void> updateRecipientDataOnDeletion(String recipientId, double grade) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(recipientId);
        CompletableFuture<Void> future = new CompletableFuture<>();

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Integer currentNumberReviewers = snapshot.child("numberReviewers").getValue(Integer.class);
                Double averageRating = snapshot.child("averageRating").getValue(Double.class);

                currentNumberReviewers--;
                averageRating = Math.round((averageRating * (currentNumberReviewers + 1) - grade) / currentNumberReviewers * 100.0) / 100.0;

                Map<String, Object> updates = new HashMap<>();
                updates.put("numberReviewers", currentNumberReviewers);
                updates.put("averageRating", averageRating);

                usersRef.updateChildren(updates, (error, ref) -> {
                    if (error != null) {
                        logDatabaseError(error, "updateRecipientDataOnDeletion");
                        future.completeExceptionally(new RuntimeException("Failed to update recipient data after feedback deletion"));
                    } else {
                        future.complete(null);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logDatabaseError(databaseError, "updateRecipientDataOnDeletion");
                future.completeExceptionally(new RuntimeException("Failed to retrieve recipient data after feedback deletion"));
            }
        });

        return future;
    }
}