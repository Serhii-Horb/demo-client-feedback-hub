package com.api.client_feedback_hub.exceptions;

public class NotFoundInDbException extends RuntimeException {
    public NotFoundInDbException(String message) {
        super(message);
    }
}
