package com.readyroad.readyroadbackend.dto;

import com.readyroad.readyroadbackend.domain.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String type = "Bearer";

    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private Role role;
}
