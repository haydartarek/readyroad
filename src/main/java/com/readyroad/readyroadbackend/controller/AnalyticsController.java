package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.ErrorPatternResponse;
import com.readyroad.readyroadbackend.dto.WeakAreaRecommendationResponse;
import com.readyroad.readyroadbackend.service.AnalyticsService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Analytics Controller - Feature C
 * 
 * <p>Provides user analytics and personalized recommendations:</p>
 * <ul>
 *   <li><b>Story C1:</b> View Error Patterns - Analyze user's mistake patterns across 6 error types</li>
 *   <li><b>Story C2:</b> Recommend Weak Areas - Top 3 weakest categories with improvement plan</li>
 * </ul>
 * 
 * <p><b>Security:</b> All endpoints require authentication (JWT in production, dev mode fallback in development)</p>
 *
 * @author ReadyRoad Team
 * @since Feature C - Analytics & Recommendations
 */
@RestController
@RequestMapping("/api/users/me/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "User analytics and recommendations API")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AuthenticationUtil authenticationUtil;

    // ═══════════════════════════════════════════════════════════════
    // 📊 ANALYTICS ENDPOINTS
    // ═══════════════════════════════════════════════════════════════

    /**
     * Story C1: View Error Patterns
     * 
     * <p><b>Endpoint:</b> GET /api/users/me/analytics/error-patterns</p>
     * 
     * <p>Returns exactly 6 supported error pattern types with statistics:</p>
     * <ol>
     *   <li>SIGN_CONFUSION - Misinterpreting road signs</li>
     *   <li>SUPPLEMENTARY_IGNORED - Missing supplementary panels</li>
     *   <li>PRIORITY_MISUNDERSTANDING - Priority rule errors</li>
     *   <li>SPEED_LIMIT_ERROR - Speed limit mistakes</li>
     *   <li>ZONE_CONFUSION - Zone-specific rule errors</li>
     *   <li>RULE_OVERGENERALIZATION - Applying rules incorrectly</li>
     * </ol>
     * 
     * @param authentication Spring Security authentication object (automatically injected)
     * @return List of 6 error patterns sorted by frequency descending
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
        // Extract user ID using AuthenticationUtil (supports dev and production modes)
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("[C1] GET /api/users/me/analytics/error-patterns - userId: {}", userId);

        // Retrieve error patterns from service
        List<ErrorPatternResponse> patterns = analyticsService.getErrorPatterns(userId);

        log.info("[C1] Returning {} error patterns for user {}", patterns.size(), userId);
        return ResponseEntity.ok(patterns);
    }

    /**
     * Story C2: Recommend Weak Areas
     * 
     * <p><b>Endpoint:</b> GET /api/users/me/analytics/weak-areas</p>
     * 
     * <p>Returns top 3 weakest categories with personalized improvement recommendations:</p>
     * <ul>
     *   <li><b>Target:</b> 80% accuracy (Belgian driving test standards)</li>
     *   <li><b>Includes:</b> Recommended question count, difficulty level, estimated time</li>
     *   <li><b>Prioritization:</b> Based on performance gap and importance</li>
     * </ul>
     * 
     * @param authentication Spring Security authentication object (automatically injected)
     * @return List of 0-3 weak area recommendations with improvement plans
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
    public ResponseEntity<List<WeakAreaRecommendationResponse>> getWeakAreaRecommendations(
            Authentication authentication) {
        
        // Extract user ID using AuthenticationUtil (supports dev and production modes)
        Long userId = authenticationUtil.extractUserId(authentication);
        log.info("[C2] GET /api/users/me/analytics/weak-areas - userId: {}", userId);

        // Retrieve weak area recommendations from service
        List<WeakAreaRecommendationResponse> recommendations = 
            analyticsService.getWeakAreaRecommendations(userId);

        log.info("[C2] Returning {} weak area recommendations for user {}", 
                 recommendations.size(), userId);
        return ResponseEntity.ok(recommendations);
    }

    // ═══════════════════════════════════════════════════════════════
    // 🔧 HELPER METHODS (Optional - if you prefer not to use AuthenticationUtil)
    // ═══════════════════════════════════════════════════════════════

    /**
     * Alternative helper method for extracting user ID directly from Authentication
     * 
     * <p><b>Note:</b> This method is kept for reference but not used in this implementation.
     * We use {@link AuthenticationUtil#extractUserId(Authentication)} instead for better
     * consistency across the application and support for dev/production modes.</p>
     * 
     * @param authentication Spring Security Authentication object
     * @return User ID
     * @throws IllegalStateException if user is not authenticated or principal type is invalid
     */
    @SuppressWarnings("unused")
    private Long extractUserIdDirect(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("User is not authenticated");
            throw new IllegalStateException("User must be authenticated");
        }

        Object principal = authentication.getPrincipal();
        
        // Handle User principal (most common case)
        if (principal instanceof com.readyroad.readyroadbackend.domain.entity.User user) {
            log.debug("Extracted user ID: {} from User principal", user.getId());
            return user.getId();
        }
        
        // Handle UserDetails principal (fallback)
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            String username = userDetails.getUsername();
            log.debug("Extracting user ID from UserDetails username: {}", username);
            
            throw new IllegalStateException(
                "Cannot extract user ID from UserDetails. Principal must be User entity.");
        }

        // Unknown principal type
        log.error("Unknown principal type: {}", principal.getClass().getName());
        throw new IllegalStateException(
            "Cannot extract user ID from authentication principal of type: " + 
            principal.getClass().getName());
    }
}
