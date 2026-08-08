package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.enums.Role;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Authentication Response DTO
 *
 * Returned after successful login or registration
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String type = "Bearer";

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private String preferredLanguage;
    private Boolean emailVerified;
    private List<String> linkedProviders;
    private Boolean newUser;
    private Instant expiresAt;
    /**
     * Static factory method to create AuthResponse
     * Replaces Lombok @Builder pattern
     */
    public static AuthResponse of(
            String token,
            Long userId,
            String username,
            String email,
            String fullName,
            Role role,
            String preferredLanguage,
            Boolean emailVerified,
            List<String> linkedProviders,
            boolean newUser,
            Instant expiresAt) {
        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setType("Bearer");
        response.setUserId(userId);
        response.setUsername(username);
        response.setEmail(email);
        response.setFullName(fullName);
        response.setRole(role);
        response.setPreferredLanguage(preferredLanguage);
        response.setEmailVerified(emailVerified);
        response.setLinkedProviders(linkedProviders);
        response.setNewUser(newUser);
        response.setExpiresAt(expiresAt);
        return response;
    }
}
