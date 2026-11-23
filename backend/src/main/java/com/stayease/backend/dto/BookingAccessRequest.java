package com.stayease.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class BookingAccessRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    // Getter
    public String getEmail() {
        return email;
    }

    // Setter
    public void setEmail(String email) {
        this.email = email;
    }
}
