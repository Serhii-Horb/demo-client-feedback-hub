package com.api.client_feedback_hub.controller;

import com.api.client_feedback_hub.exceptions.AuthorizationException;
import com.api.client_feedback_hub.exceptions.BadRequestException;
import org.modelmapper.spi.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class AdviceController {

    /**
     * Handles MethodArgumentNotValidException.
     * Returns a 400 Bad Request status with validation error messages.
     *
     * @param ex the thrown MethodArgumentNotValidException
     * @return a ResponseEntity with status 400 and a map of error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError -> {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Handles exceptions of type {@link BadRequestException}.
     * Returns a 400 Bad Request status with an error message.
     *
     * @param exception the thrown {@link BadRequestException}
     * @return a {@link ResponseEntity} with status 400 and an {@link ErrorMessage}
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorMessage> handleException(BadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorMessage(exception.getMessage()));
    }

    /**
     * Handles exceptions of type {@link AuthorizationException}.
     * Returns a 401 Unauthorized status with an error message.
     *
     * @param exception the thrown {@link AuthorizationException}
     * @return a {@link ResponseEntity} with status 401 and an {@link ErrorMessage}
     */
    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ErrorMessage> handleException(AuthorizationException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorMessage(exception.getMessage()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorMessage> handleException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorMessage(exception.getMessage()));
    }
}
