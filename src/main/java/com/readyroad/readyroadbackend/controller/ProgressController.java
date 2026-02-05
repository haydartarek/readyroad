package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.util.AuthenticationUtil;
import com.readyroad.readyroadbackend.dto.CategoryProgressResponse;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse;
import com.readyroad.readyroadbackend.service.ProgressService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Feature B: Progress Tracking
 *
 * Provides endpoints for viewing user learning progress:
 * - Story B2: View Overall Progress
 * - Story B3: View Category-Level Progress
 *
 * @author ReadyRoad Team
 * @since Phase 5 Sprint 3
 */
@RestController
@RequestMapping("/api/users/me/progress")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Progress Tracking", description = "Feature B: View learning progress and statistics")
@CrossOrigin(origins = "*")
public class ProgressController {

    private final ProgressService progressService;
    private final AuthenticationUtil authenticationUtil;

    // ============================================================================
    // STORY B2: VIEW OVERALL PROGRESS
    // ============================================================================

    /**
     * Get overall learning progress for the authenticated user
     * Story B2: View Overall Progress
     *
     * Features:
     * - Total questions attempted across all categories
     * - Overall accuracy percentage
     * - Mastery level (BEGINNER/INTERMEDIATE/ADVANCED)
     * - Weak categories (<70% accuracy, ≥5 attempts)
     * - Strong categories (>85% accuracy, ≥5 attempts)
     * - Recommended difficulty level (EASY/MEDIUM/HARD)
     * - Study streak (consecutive days)
     * - Questions remaining
     * - Completed exams summary
     *
     * GET /api/users/me/progress/overall
     *
     * @param authentication Spring Security authentication
     * @return Overall progress with statistics
     */
    @GetMapping("/overall")
    @Operation(
        summary = "Get overall learning progress (Story B2)",
        description = """
            Returns comprehensive learning progress including:
            - Total attempts and accuracy
            - Mastery level assessment
            - Weak and strong categories
            - Difficulty recommendation
            - Study streak tracking
            - Questions remaining count
            - Completed exams summary
            """,
        security = @SecurityRequirement(name = "none")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Overall progress retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = OverallProgressResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = @Content
        )
    })
    public ResponseEntity<OverallProgressResponse> getOverallProgress(
            Authentication authentication) {

        log.info("GET /api/users/me/progress/overall - Overall progress requested");

        // Extract user ID from authentication
        Long userId = authenticationUtil.extractUserId(authentication);

        // In production mode, return 401 if not authenticated
        if (userId == null) {
            log.warn("Unauthenticated access attempt to /api/users/me/progress/overall - returning 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Fetching overall progress for user {}", userId);

        // Get overall progress from service
        OverallProgressResponse response = progressService.getOverallProgress(userId);

        log.info("Overall progress retrieved for user {}: {} attempts, {}% accuracy, difficulty: {}",
            userId,
            response.getTotalAttempted(),
            response.getOverallAccuracy(),
            response.getRecommendedDifficulty()
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // STORY B3: VIEW CATEGORY-LEVEL PROGRESS
    // ============================================================================

    /**
     * Get category-level progress for the authenticated user
     * Story B3: View Category-Level Progress
     *
     * Features:
     * - Per-category statistics (attempts, accuracy)
     * - Per-category mastery levels (BEGINNER/INTERMEDIATE/ADVANCED)
     * - Weak category identification (<70% accuracy, ≥5 attempts)
     * - Strong category identification (>85% accuracy, ≥5 attempts)
     * - Per-category difficulty recommendation (EASY/MEDIUM/HARD)
     * - Last practiced timestamp
     * - Complete user isolation
     *
     * GET /api/users/me/progress/categories
     *
     * @param authentication Spring Security authentication
     * @return List of category progress entries
     */
    @GetMapping("/categories")
    @Operation(
        summary = "Get category-level progress (Story B3)",
        description = """
            Returns detailed progress for each category:
            - Category-specific statistics
            - Individual mastery levels
            - Weak/strong category flags
            - Recommended difficulty per category
            - Last practiced timestamp
            
            Key Differences:
            - Mastery Level: Past performance (entity-based, optimistic)
            - Difficulty Recommendation: Future practice (service-based, conservative)
            """,
        security = @SecurityRequirement(name = "none")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Category progress retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CategoryProgressResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401",
            description = "User not authenticated",
            content = @Content
        )
    })
    public ResponseEntity<List<CategoryProgressResponse>> getCategoryProgress(
            Authentication authentication) {

        log.info("GET /api/users/me/progress/categories - Category progress requested");

        // Extract user ID from authentication
        Long userId = authenticationUtil.extractUserId(authentication);

        // In production mode, return 401 if not authenticated
        if (userId == null) {
            log.warn("Unauthenticated access attempt to /api/users/me/progress/categories - returning 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug("Fetching category progress for user {}", userId);

        // Get category progress from service
        List<CategoryProgressResponse> response = progressService.getCategoryProgress(userId);

        log.info("Category progress retrieved for user {}: {} categories with data",
            userId,
            response.size()
        );

        return ResponseEntity.ok(response);
    }

    // ============================================================================
    // RECOMMENDATIONS ENDPOINT (Alias for Analytics)
    // ============================================================================

    /**
     * Get study recommendations (Alias for /api/users/me/analytics/weak-areas)
     * GET /api/users/me/progress/recommendations
     *
     * @param authentication Spring Security authentication
     * @return Study recommendations based on weak areas
     */
    @GetMapping("/recommendations")
    @Operation(
        summary = "Get study recommendations",
        description = "Returns personalized study recommendations based on weak areas. " +
                      "This is an alias endpoint for /api/users/me/analytics/weak-areas",
        security = @SecurityRequirement(name = "bearer-jwt")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Recommendations retrieved successfully"
        ),
        @ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Authentication required"
        )
    })
    public ResponseEntity<?> getRecommendations(Authentication authentication) {
        log.info("GET /api/users/me/progress/recommendations - Recommendations requested");

        // Extract user ID from authentication
        Long userId = authenticationUtil.extractUserId(authentication);

        // In production mode, return 401 if not authenticated
        if (userId == null) {
            log.warn("Unauthenticated access attempt to recommendations - returning 401");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Redirect to analytics service
        log.info("Fetching recommendations for user {}", userId);

        // For now, return a simple response
        // In a real implementation, this would call AnalyticsService.getWeakAreaRecommendations
        return ResponseEntity.ok(Map.of(
            "message", "Recommendations endpoint working",
            "userId", userId,
            "recommendations", List.of()
        ));
    }

    // ============================================================================
    // End of ProgressController
    // ============================================================================
}
