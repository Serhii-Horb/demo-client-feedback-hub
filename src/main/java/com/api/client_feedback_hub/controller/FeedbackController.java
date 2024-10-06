package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.dto.FeedbackRequestDto;
import com.api.client_feedback_hub.dto.FeedbackResponseDto;
import com.api.client_feedback_hub.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/feedbacks")
public class FeedbackController {
    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<FeedbackResponseDto>> getFeedbackById(@PathVariable String id) {
        return feedbackService.getFeedbackById(id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/reviewer/{id}")
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getFeedbackByReviewerId(@PathVariable String id) {
        return feedbackService.getAllFeedbacksByReviewerId(id)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/recipient/{id}")
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getAllFeedbacksByRecipientId(@PathVariable String id) {
        return feedbackService.getAllFeedbacksByRecipientId(id)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> createFeedback(@Valid @RequestBody FeedbackRequestDto feedbackRequestDto) {
        return feedbackService.createFeedback(feedbackRequestDto)
                .thenApply(feedbackId -> ResponseEntity.ok("Feedback created successfully with ID: " + feedbackId));
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<List<FeedbackResponseDto>>> getAllFeedbacks() {
        return feedbackService.getAllFeedbacks()
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<String>> deleteFeedback(@PathVariable String id) {
        return feedbackService.deleteFeedbackById(id)
                .thenApply(result -> ResponseEntity.ok("Feedback deleted successfully with ID: " + id));
    }
}