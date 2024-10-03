package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.entity.Feedback;
import com.api.client_feedback_hub.mapper.FeedbackRequestDto;
import com.api.client_feedback_hub.mapper.FeedbackResponseDto;
import com.api.client_feedback_hub.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(value = "/feedbacks")
@Tag(name = "Feedback controller", description = "Getting, deleting, or updating a feedback is done using this controller.")
@RequiredArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Creates a new feedback.",
            description = "Allows you to create a new feedback.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Feedback created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<String> createFeedbacks(@RequestBody Feedback feedback) {
        try {
            feedbackService.addFeedback(feedback.getFeedbackId(), feedback.getReviewerId(), feedback.getRating(),
                    feedback.getComment(), Long.valueOf(String.valueOf(feedback.getCreatedAt())));
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (Exception e) {
            // Логируем ошибку
            System.err.println("Error creating feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

    @DeleteMapping(value = "/delete/{feedbackId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Deletes the feedback.", description = "Allows you to delete a feedback.")
    public ResponseEntity<String> deleteFeedbackById(@PathVariable @Valid @Min(1) String feedbackId) {
        // Асинхронно удаляем пользователя и обрабатываем результат
        CompletableFuture<Boolean> deletionResult = feedbackService.deleteFeedback(feedbackId);

        // Ожидаем результат и возвращаем соответствующий HTTP статус
        return deletionResult.thenApply(deleted -> {
            if (deleted) {
                return ResponseEntity.ok("feedback deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete feedback.");
            }
        }).exceptionally(e -> {
            System.err.println("Error deleting feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting feedback: " + e.getMessage());
        }).join();
    }

    @GetMapping("/search/{feedbackId}")
    @Operation(
            summary = "Get feedback by ID.",
            description = "Fetches a feedback by its ID."
    )
    public ResponseEntity<FeedbackResponseDto> getFeedbackById(@PathVariable String feedbackId) {
        try {
            // Найдите отзыв по ID асинхронно
            CompletableFuture<Feedback> feedbackFuture = feedbackService.findFeedbackById(feedbackId);

            // Ожидаем завершения асинхронной операции и получаем результат
            Feedback feedback = feedbackFuture.get();  // Используем get() для извлечения результата

            if (feedback == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Получаем createdAt напрямую как Long
            String createdAtString = String.valueOf(feedback.getCreatedAt()); // Предполагаем, что это строка
            Long createdAt = null;

            // Проверяем и преобразуем
            try {
                createdAt = Long.parseLong(createdAtString);
            } catch (NumberFormatException e) {
                System.err.println("Error converting createdAt to Long: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }

            // Преобразуем сущность Feedback в FeedbackResponseDTO
            FeedbackResponseDto feedbackResponse = new FeedbackResponseDto(
                    feedback.getFeedbackId(),
                    feedback.getReviewerId(),
                    feedback.getRating(),
                    feedback.getComment(),
                    createdAt // Оставляем как Long
            );

            // Возвращаем DTO в качестве ответа
            return ResponseEntity.ok(feedbackResponse);
        } catch (Exception e) {
            System.err.println("Error fetching feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PutMapping("/update/{feedbackId}")
    @Operation(
            summary = "Updates a feedback.",
            description = "Allows you to update an existing feedback."
    )
    public ResponseEntity<String> updateFeedback(@PathVariable String feedbackId, @RequestBody Feedback feedback) {
        try {
            feedbackService.updateFeedback(feedbackId, feedback.getReviewerId(), feedback.getRating(),
                    feedback.getComment(), Long.valueOf(String.valueOf(feedback.getCreatedAt())));
            return ResponseEntity.status(HttpStatus.OK).build();
        } catch (Exception e) {
            System.err.println("Error updating feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }

}
