package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.UserProfileResponse;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Profile Controller
 * Handles user profile operations
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Profile", description = "User profile management API")
public class UserController {

    private final UserRepository userRepository;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping("/me")
    @Operation(
        summary = "Get current user profile",
        description = "Returns the authenticated user's profile information",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved user profile",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = UserProfileResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content
        )
    })
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("GET /api/users/me - userId: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> {
                log.error("User not found: {}", userId);
                return new RuntimeException("User not found");
            });

        UserProfileResponse response = UserProfileResponse.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .role(user.getRole().name())
            .isActive(user.getIsActive())
            .createdAt(user.getCreatedAt())
            .build();

        log.info("User profile retrieved: {} ({})", user.getUsername(), user.getRole());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    @Operation(
        summary = "Delete current user account",
        description = "Permanently deletes the authenticated user account and all associated data via DB cascade",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("DELETE /api/users/me - userId: {}", userId);

        if (!userRepository.existsById(userId)) {
            log.error("User not found for deletion: {}", userId);
            return ResponseEntity.notFound().build();
        }

        userRepository.deleteById(userId);
        log.info("User account permanently deleted: userId={}", userId);

        return ResponseEntity.noContent().build();
    }
}
