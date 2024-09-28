package com.api.client_feedback_hub.entity.enums;

public enum Role {
    /**
     * Standard user role with basic access rights.
     */
    USER("User"),

    /**
     * Administrator role with elevated privileges and access rights.
     */
    ADMINISTRATOR("Administrator");

    /**
     * The name of the role.
     */
    private String role;

    /**
     * Constructor to initialize the role name.
     *
     * @param role The name of the role.
     */
    Role(String role) {
        this.role = role;
    }

    /**
     * Gets the name of the role.
     *
     * @return The name of the role.
     */
    public String getRole() {
        return role;
    }
}
