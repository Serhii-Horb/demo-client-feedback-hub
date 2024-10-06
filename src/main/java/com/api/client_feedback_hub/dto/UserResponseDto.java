package com.api.client_feedback_hub.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private Double averageRating;
    private int numberReviewers;
}
