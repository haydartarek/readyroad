package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.GoogleAuthExchangeRequest;
import com.readyroad.readyroadbackend.dto.UpdateUserProfileRequest;
import com.readyroad.readyroadbackend.dto.UpdatePreferredLanguageRequest;
import com.readyroad.readyroadbackend.dto.UserProfileResponse;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.SocialAuthService;
import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Map;

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
    private final AuthIdentityRepository authIdentityRepository;
    private final AuthenticationUtil authenticationUtil;
    private final SocialAuthService socialAuthService;
    private final BackendMessageService messages;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile", description = "Returns the authenticated user's profile information", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user profile", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<UserProfileResponse> getCurrentUserProfile(Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("GET /api/users/me - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
                });

        log.info("User profile retrieved: {} ({})", user.getUsername(), user.getRole());
        return ResponseEntity.ok(toUserProfileResponse(user));
    }

    @PostMapping("/me/auth-identities/google/link")
    public ResponseEntity<UserProfileResponse> linkGoogleIdentity(
            Authentication authentication,
            @Valid @RequestBody GoogleAuthExchangeRequest request) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("POST /api/users/me/auth-identities/google/link - userId: {}", userId);

        User user = socialAuthService.linkGoogleToCurrentUser(userId, request);
        return ResponseEntity.ok(toUserProfileResponse(user));
    }

    @Transactional
    @PutMapping("/me")
    @Operation(summary = "Update current user profile", description = "Updates the authenticated user's editable profile fields", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user profile", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserProfileResponse.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed", content = @Content),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Email already in use", content = @Content)
    })
    public ResponseEntity<?> updateCurrentUserProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateUserProfileRequest request) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("PUT /api/users/me - userId: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found: {}", userId);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
                });

        String normalizedFullName = request.fullName().trim().replaceAll("\\s+", " ");
        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);

        boolean emailChanged = !user.getEmail().equalsIgnoreCase(normalizedEmail);
        if (emailChanged && userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            log.warn("Profile update rejected: email already in use for userId={}, email={}", userId, normalizedEmail);
            throw new ResponseStatusException(HttpStatus.CONFLICT, messages.get("user.email_in_use"));
        }

        user.setFullName(normalizedFullName);
        user.setEmail(normalizedEmail);

        User savedUser;
        try {
            savedUser = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            log.warn("Profile update conflict (concurrent email claim): userId={}, email={}", userId, normalizedEmail);
            throw new ResponseStatusException(HttpStatus.CONFLICT, messages.get("user.email_in_use"));
        }
        log.info("User profile updated successfully: userId={}, username={}", userId, savedUser.getUsername());

        return ResponseEntity.ok(toUserProfileResponse(savedUser));
    }

    @Transactional
    @PatchMapping("/me/preferred-language")
    @Operation(
            summary = "Update preferred language",
            description = "Persists the authenticated user's explicit language choice",
            security = @SecurityRequirement(name = "bearer-jwt"))
    public ResponseEntity<UserProfileResponse> updatePreferredLanguage(
            Authentication authentication,
            @Valid @RequestBody UpdatePreferredLanguageRequest request) {
        Long userId = authenticationUtil.extractUserId(authentication);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        messages.get("auth.user_not_found")));

        user.setPreferredLanguage(request.preferredLanguage());
        User savedUser = userRepository.save(user);
        return ResponseEntity.ok(toUserProfileResponse(savedUser));
    }

    @DeleteMapping("/me")
    @Operation(summary = "Delete current user account", description = "Permanently deletes the authenticated user account and all associated data via DB cascade", security = @SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
    })
    public ResponseEntity<Void> deleteCurrentUser(Authentication authentication) {
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("DELETE /api/users/me - userId: {}", userId);

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            log.error("User not found for deletion: {}", userId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, messages.get("auth.user_not_found"));
        }

        if (user.getRole() == Role.ADMIN || user.getRole() == Role.MODERATOR) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    messages.get("user.role_account_deletion_forbidden"));
        }

        userRepository.delete(user);
        log.info("User account permanently deleted: userId={}", userId);

        return ResponseEntity.noContent().build();
    }

    private UserProfileResponse toUserProfileResponse(User user) {
        var linkedProviders = authIdentityRepository.findByUserId(user.getId()).stream()
                .map(identity -> identity.getProvider().name())
                .sorted()
                .toList();

        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .preferredLanguage(user.getPreferredLanguage())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .createdAt(user.getCreatedAt())
                .linkedProviders(linkedProviders)
                .googleLinked(linkedProviders.contains(AuthProvider.GOOGLE.name()))
                .build();
    }
}
