package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User Profile Response DTO
 * Contains user profile information for API responses
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private String preferredLanguage;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private List<String> linkedProviders;
    private Boolean googleLinked;
}
