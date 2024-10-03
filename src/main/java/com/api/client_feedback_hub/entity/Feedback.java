package com.api.client_feedback_hub.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private String feedbackId;
    private String reviewerId;
    private int rating;
    private String comment;
    private Long createdAt;

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("feedbackId", feedbackId);
        result.put("reviewerId", reviewerId);
        result.put("rating", rating);
        result.put("comment", comment);
        result.put("createdAt", createdAt.toString());  // Преобразование LocalDateTime в строку
        return result;
    }
}
