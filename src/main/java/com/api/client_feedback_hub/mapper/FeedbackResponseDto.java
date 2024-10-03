package com.api.client_feedback_hub.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponseDto {
    private String feedbackId;
    private String reviewerId;
    private int rating;
    private String comment;
    private Long createdAt;
}
