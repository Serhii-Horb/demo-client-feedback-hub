package com.api.client_feedback_hub.mapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequestDto {
    private String reviewerId;
    private String recipientId;
    private String feedbackText;
    private int rating;
}
