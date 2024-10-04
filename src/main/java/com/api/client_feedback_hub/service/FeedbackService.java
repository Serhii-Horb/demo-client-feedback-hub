package com.api.client_feedback_hub.service;

import com.api.client_feedback_hub.dto.FeedbackRequestDto;
import com.api.client_feedback_hub.dto.FeedbackResponseDto;
import com.api.client_feedback_hub.mapper.FeedbackMapper;
import com.api.client_feedback_hub.model.Feedback;
import com.google.firebase.database.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FeedbackService {
    @Autowired
    FeedbackMapper feedbackMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public CompletableFuture<List<FeedbackResponseDto>> getAllFeedbacks() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        List<FeedbackResponseDto> feedbackList = new ArrayList<>();
        CompletableFuture<List<FeedbackResponseDto>> futureFeedbacks = new CompletableFuture<>();

        logger.info("Starting to fetch all feedbacks from database");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                logger.info("DataSnapshot received from database");
                if (dataSnapshot.exists()) {
                    logger.info("Data found, processing feedbacks...");
                    // Iterate through each child in the snapshot
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logger.info("User added: {}", feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object");
                        }
                    });
                    // Complete the future with the list of feedbacks
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(feedbackList);
                } else {
                    // Complete the future with no data
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error: {}", databaseError.getMessage());
                futureFeedbacks.completeExceptionally(new RuntimeException("Database read failed"));
            }
        });
        return futureFeedbacks;
    }

    public CompletableFuture<FeedbackResponseDto> getFeedbackById(String id) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("feedbacks");
        CompletableFuture<FeedbackResponseDto> future = new CompletableFuture<>();

        // Check if the feedback exists in the database
        ref.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Feedback feedback = dataSnapshot.getValue(Feedback.class);
                    if (feedback != null) {
                        // Complete the future with the feedback data
                        future.complete(feedbackMapper.convertToDto(feedback));
                    } else {
                        // Complete exceptionally if parsing fails
                        future.completeExceptionally(new RuntimeException("Feedback data found but failed to parse it"));
                    }
                } else {
                    // Feedback does not exist, complete future exceptionally
                    logger.warn("No feedback found with ID: {}", id);
                    future.completeExceptionally(new RuntimeException("No feedback found with the provided ID: " + id));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error occurred while retrieving feedback with ID {}: {}", id, databaseError.getMessage());
                // Complete exceptionally on cancellation
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
                logger.info("DataSnapshot received from database");
                if (dataSnapshot.exists()) {
                    logger.info("Data found, processing feedbacks...");
                    // Iterate through each child in the snapshot
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logger.info("Feedback added: {}", feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object");
                        }
                    });
                    // Complete the future with the list of users
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(feedbackList);
                } else {
                    // Complete the future with no data
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error: {}", databaseError.getMessage());
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
                logger.info("DataSnapshot received from database");
                if (dataSnapshot.exists()) {
                    logger.info("Data found, processing feedbacks...");
                    // Iterate through each child in the snapshot
                    dataSnapshot.getChildren().forEach(snapshot -> {
                        Feedback feedback = snapshot.getValue(Feedback.class);
                        if (feedback != null) {
                            feedbackList.add(feedbackMapper.convertToDto(feedback));
                            logger.info("Feedback added: {}", feedback.getFeedbackId());
                        } else {
                            logger.warn("Failed to parse data as Feedback object");
                        }
                    });
                    // Complete the future with the list of users
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(feedbackList);
                } else {
                    // Complete the future with no data
                    logger.info("No data found, returning an empty list");
                    futureFeedbacks.complete(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                logger.error("Database error: {}", databaseError.getMessage());
                futureFeedbacks.completeExceptionally(new RuntimeException("Database read failed"));
            }
        });
        return futureFeedbacks;
    }


    public CompletableFuture<String> createFeedback(FeedbackRequestDto feedbackRequestDto) {
        // Get references to the "users" and "feedbacks" nodes in Firebase
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks");
        CompletableFuture<String> future = new CompletableFuture<>();

        // Check if the reviewerId exists
        usersRef.child(feedbackRequestDto.getReviewerId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot reviewerSnapshot) {
                if (!reviewerSnapshot.exists()) {
                    // If reviewer ID does not exist, complete the future exceptionally
                    future.completeExceptionally(new RuntimeException("Reviewer ID does not exist: " + feedbackRequestDto.getReviewerId()));
                    return;
                }
                // Check if the recipientId exists
                usersRef.child(feedbackRequestDto.getRecipientId()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot recipientSnapshot) {
                        if (!recipientSnapshot.exists()) {
                            // If recipient ID does not exist, complete the future exceptionally
                            future.completeExceptionally(new RuntimeException("Recipient ID does not exist: " + feedbackRequestDto.getRecipientId()));
                            return;
                        }
                        // Both IDs exist; now create a new feedback entry
                        String uniqueFeedbackId = feedbackRef.push().getKey(); // Generate a unique ID for the feedback
                        Feedback newFeedback = new Feedback(uniqueFeedbackId,
                                feedbackRequestDto.getReviewerId(),
                                feedbackRequestDto.getRecipientId(),
                                feedbackRequestDto.getFeedbackText(),
                                feedbackRequestDto.getGrade(),
                                System.currentTimeMillis());

                        // Retrieve current number of reviewers and average rating from the recipient's data
                        Integer currentNumberReviewers = recipientSnapshot.child("numberReviewers").getValue(Integer.class);
                        Double averageRating = recipientSnapshot.child("averageRating").getValue(Double.class);

                        // Increment the number of reviewers
                        currentNumberReviewers++;

                        // Calculate the new average rating based on the current rating
                        averageRating = Math.round((averageRating * (currentNumberReviewers - 1) + feedbackRequestDto.getGrade()) / currentNumberReviewers * 100.0) / 100.0;

                        // Create a map for updating the values
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("numberReviewers", currentNumberReviewers);
                        updates.put("averageRating", averageRating);

                        // Update the user's numberReviewers and averageRating in the database
                        usersRef.child(feedbackRequestDto.getRecipientId()).updateChildren(updates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    // Handle errors when updating user data
                                    logger.error("Failed to update user data: {}", databaseError.getMessage());
                                    future.completeExceptionally(new RuntimeException("Failed to update user data: " + databaseError.getMessage()));
                                } else {
                                    // User data successfully updated, now save the feedback
                                    feedbackRef.child(uniqueFeedbackId).setValue(newFeedback, new DatabaseReference.CompletionListener() {
                                        @Override
                                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                            if (databaseError != null) {
                                                // Log error if saving feedback fails
                                                logger.error("Failed to save feedback: {}", databaseError.getMessage());
                                                future.completeExceptionally(new RuntimeException("Feedback creation failed: " + databaseError.getMessage()));
                                            } else {
                                                // Log success message if feedback is saved successfully
                                                logger.info("Feedback successfully saved with ID: {}", uniqueFeedbackId);
                                                future.complete("Feedback created with ID: " + uniqueFeedbackId);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Log the error if the recipientId check is canceled
                        logger.error("Failed to check recipient ID: {}", databaseError.getMessage());
                        future.completeExceptionally(new RuntimeException("Failed to check recipient ID"));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log the error if the reviewerId check is canceled
                logger.error("Failed to check reviewer ID: {}", databaseError.getMessage());
                future.completeExceptionally(new RuntimeException("Failed to check reviewer ID"));
            }
        });

        return future; // Return the CompletableFuture for further processing
    }

    public CompletableFuture<Void> deleteFeedbackById(String feedbackId) {
        DatabaseReference feedbackRef = FirebaseDatabase.getInstance().getReference("feedbacks").child(feedbackId);
        CompletableFuture<Void> future = new CompletableFuture<>();

        // Check if the feedback with the given ID exists
        feedbackRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot feedbackSnapshot) {
                if (!feedbackSnapshot.exists()) {
                    // If feedback does not exist, complete the future exceptionally
                    logger.error("Feedback with ID: {} does not exist.", feedbackId);
                    future.completeExceptionally(new RuntimeException("Feedback with ID: " + feedbackId + " does not exist."));
                    return;
                }

                // Retrieve feedback data, including recipientId
                Feedback feedback = feedbackSnapshot.getValue(Feedback.class);
                if (feedback == null || feedback.getRecipientId() == null) {
                    future.completeExceptionally(new RuntimeException("Failed to parse feedback data or recipientId is missing."));
                    return;
                }

                String recipientId = feedback.getRecipientId();

                // Reference to the recipient's user data
                DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users").child(recipientId);

                // Retrieve the current number of reviewers and average rating from the recipient's data
                usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot recipientSnapshot) {
                        Integer currentNumberReviewers = recipientSnapshot.child("numberReviewers").getValue(Integer.class);
                        Double averageRating = recipientSnapshot.child("averageRating").getValue(Double.class);

                        // Recalculate the average rating after removing the feedback
                        currentNumberReviewers--;
                        averageRating = Math.round((averageRating * (currentNumberReviewers + 1) - feedback.getGrade()) / currentNumberReviewers * 100.0) / 100.0;

                        // Create a map for updating the values
                        Map<String, Object> updates = new HashMap<>();
                        updates.put("numberReviewers", currentNumberReviewers);
                        updates.put("averageRating", averageRating);

                        // Update the recipient's data in the database
                        usersRef.updateChildren(updates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    // Log error if updating recipient data fails
                                    logger.error("Failed to update recipient's data. Error: {}", databaseError.getMessage());
                                    future.completeExceptionally(new RuntimeException("Failed to update recipient's data: " + databaseError.getMessage()));
                                    return;
                                }

                                // Proceed to delete the feedback
                                feedbackRef.removeValue(new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError != null) {
                                            // Log error if feedback deletion fails
                                            logger.error("Failed to delete feedback with ID: {}. Error: {}", feedbackId, databaseError.getMessage());
                                            future.completeExceptionally(new RuntimeException("Failed to delete feedback with ID: " + feedbackId + ". Error: " + databaseError.getMessage()));
                                        } else {
                                            // Log success if feedback was deleted
                                            logger.info("Feedback with ID: {} was deleted successfully.", feedbackId);
                                            future.complete(null); // Complete the future normally
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Log error if retrieving recipient data is cancelled
                        logger.error("Failed to check recipient data for rating update. Error: {}", databaseError.getMessage());
                        future.completeExceptionally(new RuntimeException("Failed to check recipient data: " + databaseError.getMessage()));
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Log error if feedback ID check is cancelled
                logger.error("Failed to check feedback ID: {}. Error: {}", feedbackId, databaseError.getMessage());
                future.completeExceptionally(new RuntimeException("Failed to check feedback ID: " + feedbackId + ". Error: " + databaseError.getMessage()));
            }
        });

        return future;
    }
}