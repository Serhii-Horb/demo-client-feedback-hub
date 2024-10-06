package com.api.client_feedback_hub.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    @Min(value = 1, message = "Grade must be at least 1")
    @Max(value = 5, message = "Grade must be no more than 5")
    private int grade;

    @AssertTrue(message = "Reviewer ID must not be the same as Recipient ID")
    public boolean isReviewerIdNotEqualToRecipientId() {
        return reviewerId == null || recipientId == null || !reviewerId.equals(recipientId);
    }
}
