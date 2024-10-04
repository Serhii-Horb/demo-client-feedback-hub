package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.dto.FeedbackRequestDto;
import com.api.client_feedback_hub.dto.FeedbackResponseDto;
import com.api.client_feedback_hub.service.FeedbackService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    private static final Logger logger = LoggerFactory.getLogger(FeedbackService.class);

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<FeedbackResponseDto>> getFeedbackById(@PathVariable String id) {
        return feedbackService.getFeedbackById(id)
                .thenApply(feedback -> ResponseEntity.ok(feedback))
                .exceptionally(ex -> {
                    logger.error("Error retrieving feedback: {}", ex.getMessage());
                    // Check if the error message indicates the feedback was not found
                    if (ex.getMessage().contains("No feedback found with the provided ID")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
                    }
                });
    }


    @GetMapping("/reviewer/{id}")
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getFeedbackByReviewerId(@PathVariable String id) {
        return feedbackService.getAllFeedbacksByReviewerId(id)
                .thenApply(feedback -> ResponseEntity.ok(feedback))
                .exceptionally(ex -> {
                    logger.error("Error retrieving feedback: {}", ex.getMessage());
                    // Check if the error message indicates the feedback was not found
                    if (ex.getMessage().contains("No feedback found with the provided ID")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
                    }
                });
    }

    @GetMapping("/recipient/{id}")
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getAllFeedbacksByRecipientId(@PathVariable String id) {
        return feedbackService.getAllFeedbacksByRecipientId(id)
                .thenApply(feedback -> ResponseEntity.ok(feedback))
                .exceptionally(ex -> {
                    logger.error("Error retrieving feedback: {}", ex.getMessage());
                    // Check if the error message indicates the feedback was not found
                    if (ex.getMessage().contains("No feedback found with the provided ID")) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(null);
                    } else {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(null);
                    }
                });
    }


    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createFeedback(@Valid @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.createFeedback(feedbackRequestDto)
                .thenApply(feedbackId -> ResponseEntity.ok("Feedback created successfully with ID: " + feedbackId))
                .exceptionally(ex -> {
                    // Log the error
                    logger.error("Error creating feedback: {}", ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create feedback: " + ex.getMessage());
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks()
                .thenApply(feedback -> ResponseEntity.ok(feedback))
                .exceptionally(e -> {
                    logger.error("Failed to fetch feedbacks: {}", e.getMessage());
                    // Handle errors
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteFeedback(@PathVariable String id) {
        return feedbackService.deleteFeedbackById(id)
                .thenApply(result -> ResponseEntity.ok("Feedback deletion requested for ID: " + id))
                .exceptionally(e -> {
                    logger.error("Error deleting feedback with ID: {}. Error: {}", id, e.getMessage());
                    // Handle errors
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error deleting feedback with ID: " + id);
                });
    }
}