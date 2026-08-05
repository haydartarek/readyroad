package com.readyroad.readyroadbackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AdminCreateUserRequest(
        @NotBlank @Size(max = 50) String firstName,
        @NotBlank @Size(max = 50) String lastName,
        @NotBlank @Email @Size(max = 100) String email,
        @NotBlank @Size(min = 4, max = 20)
        @Pattern(regexp = "^[a-zA-Z0-9_]+$") String username,
        @NotBlank @Size(min = 8, max = 100)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$") String password,
        @NotBlank @Pattern(regexp = "^(en|nl|fr|ar)$") String preferredLanguage,
        @NotBlank @Pattern(regexp = "^(USER|MODERATOR|ADMIN)$") String role,
        @NotNull Boolean isActive,
        @NotNull Boolean emailVerified) {
}
