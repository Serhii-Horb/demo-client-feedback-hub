package com.api.client_feedback_hub.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private String feedbackId;
    private String reviewerId;
    private String recipientId;
    private String feedbackText;
    private int grade;
    private Long timestamp;
}
