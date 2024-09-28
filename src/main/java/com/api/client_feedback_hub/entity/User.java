package com.api.client_feedback_hub.entity;

import com.api.client_feedback_hub.entity.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "users")
@Entity
@Getter
@Setter
//@ToString(exclude = {"role", "cart", "favorites", "orders"})
//@EqualsAndHashCode(exclude = {"role", "cart", "favorites", "orders"})
@AllArgsConstructor
@NoArgsConstructor
public class User {

    /**
     * Email address of the user.
     * This field must be unique and cannot be null.
     */
    @Column(name = "Email", unique = true, nullable = false)
    private String email;

    /**
     * Name of the user.
     * This field cannot be null.
     */
    @Column(name = "Name", nullable = false)
    private String name;

    /**
     * Phone number of the user.
     */
    @Column(name = "PhoneNumber")
    private String phoneNumber;

    /**
     * Role of the user.
     * This field cannot be null.
     * The role is represented as a string in the database.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "Role", nullable = false)
    private Role role;

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Long userId;
}
