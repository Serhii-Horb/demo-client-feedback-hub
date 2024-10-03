package com.api.client_feedback_hub.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    //    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String userId;
    private String email;
    private String name;
    private String phoneNumber;
    private String role;
    private String hashedPassword;
    Double averageRating;

    public User(String email, String name, String phoneNumber, String hashedPassword) {
        this.email = email;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.hashedPassword = hashedPassword;
    }
}