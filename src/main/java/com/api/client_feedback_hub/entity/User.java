package com.api.client_feedback_hub.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String role;
    private String hashedPassword;
    private Double averageRating;
    private int numberReviewers;
}