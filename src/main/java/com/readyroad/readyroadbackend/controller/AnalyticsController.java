package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.ErrorPatternResponse;
import com.readyroad.readyroadbackend.dto.WeakAreaRecommendationResponse;
import com.readyroad.readyroadbackend.service.AnalyticsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Analytics Controller - Feature C
 * Story C1: View Error Patterns
 * Story C2: Recommend Weak Areas
 */
@RestController
@RequestMapping("/api/users/me/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "User analytics and recommendations API")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    /**
     * Story C1: View Error Patterns
     * GET /api/users/me/analytics/error-patterns
     *
     * Returns exactly 6 supported error pattern types with statistics
     */
    @GetMapping("/error-patterns")
    @Operation(
        summary = "Get error pattern analytics",
        description = "Returns user's mistake patterns across 6 error types: SIGN_CONFUSION, SUPPLEMENTARY_IGNORED, PRIORITY_MISUNDERSTANDING, SPEED_LIMIT_ERROR, ZONE_CONFUSION, RULE_OVERGENERALIZATION. Sorted by frequency descending.",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved error patterns (always returns 6 items)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorPatternResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required in secure mode",
            content = @Content
        )
    })
    public ResponseEntity<List<ErrorPatternResponse>> getErrorPatterns(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("[C1] GET /api/users/me/analytics/error-patterns - userId: {}", userId);

        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(userId);

        log.info("[C1] Returning {} error patterns for user {}", patterns.size(), userId);
        return ResponseEntity.ok(patterns);
    }

    /**
     * Story C2: Recommend Weak Areas
     * GET /api/users/me/analytics/weak-areas
     *
     * Returns top 3 weakest categories with personalized improvement recommendations
     */
    @GetMapping("/weak-areas")
    @Operation(
        summary = "Get weak area recommendations",
        description = "Returns top 3 weakest categories with personalized improvement plan. Includes recommended questions, difficulty level, and estimated time. Target accuracy is 80% (Belgian standards).",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Successfully retrieved weak area recommendations (0-3 items)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = WeakAreaRecommendationResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required in secure mode",
            content = @Content
        )
    })
    public ResponseEntity<List<WeakAreaRecommendationResponse>> getWeakAreaRecommendations(Authentication authentication) {
        Long userId = extractUserId(authentication);
        log.info("[C2] GET /api/users/me/analytics/weak-areas - userId: {}", userId);

        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(userId);

        log.info("[C2] Returning {} weak area recommendations for user {}", recommendations.size(), userId);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Extract user ID from authentication (works in both dev and secure modes)
     */
    private Long extractUserId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            log.warn("No authentication found, this should not happen if security is configured correctly");
            return 1L; // Fallback (should not reach here in production)
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (NumberFormatException e) {
            log.error("Invalid user ID in authentication: {}", authentication.getName());
            throw new IllegalStateException("Invalid user ID in authentication");
        }
    }
}
