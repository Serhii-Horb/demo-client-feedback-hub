package com.api.client_feedback_hub.entity;

import com.api.client_feedback_hub.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @JsonProperty("Email")
    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    @JsonProperty("Name")
    @Column(name = "Name", nullable = false)
    private String name;

    @JsonProperty("PhoneNumber")
    @Column(name = "PhoneNumber")
    private String phoneNumber;

    @JsonProperty("Role")
    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private Role role;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    @JsonProperty("UserID")
    private Long userId;
}