package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.dto.WeakAreaRecommendationResponse;
import com.readyroad.readyroadbackend.service.AnalyticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Story C2: Recommend Weak Areas - BDD Integration Tests
 * Tests weak area recommendation functionality
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Story C2: Recommend Weak Areas - Integration Tests")
public class StoryC2WeakAreasIntegrationTest {

    @Autowired
    private AnalyticsService analyticsService;

    @Autowired
    private UserCategoryProgressRepository progressRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long testUserId;
    private Category speedLimitsCategory;
    private Category parkingCategory;
    private Category priorityCategory;
    private Category signsCategory;
    private Category zonesCategory;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // Create 5 test categories
        speedLimitsCategory = createCategory("SPEED", "Speed Limits", 1);
        parkingCategory = createCategory("PARKING", "Parking Rules", 2);
        priorityCategory = createCategory("PRIORITY", "Priority Rules", 3);
        signsCategory = createCategory("SIGNS", "Traffic Signs", 4);
        zonesCategory = createCategory("ZONES", "Zone Regulations", 5);
    }

    @Test
    @DisplayName("@C2 returns exactly 3 weakest categories")
    void returnsExactlyThreeWeakestCategories() {
        // Given: User has progress across at least 5 categories
        // And: There are at least 3 categories with measurable accuracy
        createProgress(testUserId, speedLimitsCategory.getId(), 20, 10, 50.0); // 50%
        createProgress(testUserId, parkingCategory.getId(), 15, 9, 60.0);      // 60%
        createProgress(testUserId, priorityCategory.getId(), 10, 7, 70.0);     // 70%
        createProgress(testUserId, signsCategory.getId(), 10, 8, 80.0);        // 80%
        createProgress(testUserId, zonesCategory.getId(), 10, 9, 90.0);        // 90%

        // When: I request weak area recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: Response should contain exactly 3 items
        assertThat(recommendations).hasSize(3);

        // And: Each item should have all required fields
        for (WeakAreaRecommendationResponse rec : recommendations) {
            assertThat(rec.getCategoryName()).isNotEmpty();
            assertThat(rec.getCurrentAccuracy()).isNotNull();
            assertThat(rec.getTargetAccuracy()).isNotNull();
            assertThat(rec.getRecommendedQuestions()).isNotNull().isPositive();
            assertThat(rec.getRecommendedDifficulty()).isNotEmpty();
            assertThat(rec.getEstimatedTimeMinutes()).isNotNull().isPositive();
            assertThat(rec.getPriority()).isNotNull().isBetween(1, 3);
        }

        // And: The 3 weakest should be Speed Limits (50%), Parking (60%), Priority (70%)
        assertThat(recommendations.get(0).getCategoryName()).isEqualTo("Speed Limits");
        assertThat(recommendations.get(1).getCategoryName()).isEqualTo("Parking Rules");
        assertThat(recommendations.get(2).getCategoryName()).isEqualTo("Priority Rules");
    }

    @Test
    @DisplayName("@C2 is sorted from weakest to stronger")
    void isSortedFromWeakestToStronger() {
        // Given: User has category accuracies where Parking is lower than Speed Limits
        createProgress(testUserId, speedLimitsCategory.getId(), 20, 16, 80.0); // 80%
        createProgress(testUserId, parkingCategory.getId(), 20, 10, 50.0);     // 50% - weakest
        createProgress(testUserId, priorityCategory.getId(), 20, 14, 70.0);    // 70%

        // When: I request weak area recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: First item currentAccuracy should be less than or equal to second item
        assertThat(recommendations).isNotEmpty();
        assertThat(recommendations.size()).isGreaterThanOrEqualTo(2);

        for (int i = 0; i < recommendations.size() - 1; i++) {
            assertThat(recommendations.get(i).getCurrentAccuracy())
                .as("Recommendation %d accuracy should be <= recommendation %d accuracy", i, i + 1)
                .isLessThanOrEqualTo(recommendations.get(i + 1).getCurrentAccuracy());
        }

        // And: Specifically, Parking (50%) should be first
        assertThat(recommendations.get(0).getCategoryName()).isEqualTo("Parking Rules");
        assertThat(recommendations.get(0).getCurrentAccuracy()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("@C2 returns a fixed targetAccuracy of 80.0")
    void returnsFixedTargetAccuracyOf80() {
        // Given: User has at least 3 categories with currentAccuracy below 80
        createProgress(testUserId, speedLimitsCategory.getId(), 20, 10, 50.0);
        createProgress(testUserId, parkingCategory.getId(), 20, 12, 60.0);
        createProgress(testUserId, priorityCategory.getId(), 20, 14, 70.0);

        // When: I request weak area recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: Every item targetAccuracy should be 80.0
        assertThat(recommendations).isNotEmpty();
        for (WeakAreaRecommendationResponse rec : recommendations) {
            assertThat(rec.getTargetAccuracy())
                .as("Category %s should have target accuracy of 80.0", rec.getCategoryName())
                .isEqualTo(80.0);
        }
    }

    @Test
    @DisplayName("@C2 recommendedDifficulty aligns with low accuracy")
    void recommendedDifficultyAlignsWithLowAccuracy() {
        // Given: A weakest category has currentAccuracy less than 70.0
        createProgress(testUserId, speedLimitsCategory.getId(), 20, 12, 60.0); // 60% - below 70
        createProgress(testUserId, parkingCategory.getId(), 20, 15, 75.0);     // 75%
        createProgress(testUserId, priorityCategory.getId(), 20, 16, 80.0);    // 80%

        // When: I request weak area recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: The weakest category recommendedDifficulty should be "EASY"
        assertThat(recommendations).isNotEmpty();
        WeakAreaRecommendationResponse weakestCategory = recommendations.get(0);

        assertThat(weakestCategory.getCategoryName()).isEqualTo("Speed Limits");
        assertThat(weakestCategory.getCurrentAccuracy()).isLessThan(70.0);
        assertThat(weakestCategory.getRecommendedDifficulty()).isEqualTo("EASY");
    }

    @Test
    @DisplayName("@C2 user isolation is enforced")
    void userIsolationIsEnforced() {
        // Given: User 888 has their own progress
        Long user888 = 888L;
        createProgress(user888, speedLimitsCategory.getId(), 20, 10, 50.0);
        createProgress(user888, parkingCategory.getId(), 20, 12, 60.0);

        // And: Another user (999) has different progress
        Long user999 = 999L;
        createProgress(user999, speedLimitsCategory.getId(), 20, 18, 90.0);
        createProgress(user999, parkingCategory.getId(), 20, 19, 95.0);

        // When: I request recommendations for user 888
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(user888);

        // Then: Recommendations must reflect only user 888 progress
        assertThat(recommendations).isNotEmpty();

        WeakAreaRecommendationResponse speedLimitsRec = recommendations.stream()
            .filter(r -> r.getCategoryName().equals("Speed Limits"))
            .findFirst()
            .orElseThrow();

        // User 888 has 50% in Speed Limits, not 90%
        assertThat(speedLimitsRec.getCurrentAccuracy()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("@C2 returns empty list for user with no progress")
    void returnsEmptyListForUserWithNoProgress() {
        // Given: User has no progress
        Long newUserId = 999L;

        // When: I request recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(newUserId);

        // Then: Response should be empty
        assertThat(recommendations).isEmpty();
    }

    @Test
    @DisplayName("@C2 returns empty list when no category has sufficient attempts")
    void returnsEmptyListWhenNoSufficientAttempts() {
        // Given: User has progress but with < 5 attempts per category (insufficient)
        createProgress(testUserId, speedLimitsCategory.getId(), 3, 2, 66.7);
        createProgress(testUserId, parkingCategory.getId(), 4, 2, 50.0);

        // When: I request recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: Response should be empty (need >= 5 attempts for measurable accuracy)
        assertThat(recommendations).isEmpty();
    }

    @Test
    @DisplayName("@C2 returns fewer than 3 if only 1-2 categories are weak")
    void returnsFewerThanThreeIfOnlyOneTwoCategoriesWeak() {
        // Given: User has only 2 categories with measurable progress
        createProgress(testUserId, speedLimitsCategory.getId(), 10, 5, 50.0);
        createProgress(testUserId, parkingCategory.getId(), 10, 6, 60.0);

        // When: I request recommendations
        List<WeakAreaRecommendationResponse> recommendations = analyticsService.getWeakAreaRecommendations(testUserId);

        // Then: Response should contain 2 items (not 3, because only 2 measurable)
        assertThat(recommendations).hasSize(2);

        // And: They should be sorted by accuracy
        assertThat(recommendations.get(0).getCurrentAccuracy()).isEqualTo(50.0);
        assertThat(recommendations.get(1).getCurrentAccuracy()).isEqualTo(60.0);
    }

    // Helper methods

    private Category createCategory(String code, String nameEn, int displayOrder) {
        Category category = new Category();
        category.setCode(code);
        category.setNameEn(nameEn);
        category.setNameAr(nameEn + " AR");
        category.setNameNl(nameEn + " NL");
        category.setNameFr(nameEn + " FR");
        category.setIsActive(true);
        category.setDisplayOrder(displayOrder);
        return categoryRepository.save(category);
    }

    private void createProgress(Long userId, Long categoryId, int attempted, int correct, double accuracy) {
        UserCategoryProgress progress = new UserCategoryProgress();
        progress.setUserId(userId);
        progress.setCategoryId(categoryId);
        progress.setQuestionsAttempted(attempted);
        progress.setCorrectAnswers(correct);
        progress.setAccuracyRate(accuracy);
        progress.setMasteryLevel(determineMasteryLevel(accuracy));
        progress.setLastPracticed(LocalDateTime.now());
        progressRepository.save(progress);
    }

    private UserCategoryProgress.MasteryLevel determineMasteryLevel(double accuracy) {
        if (accuracy < 50) {
            return UserCategoryProgress.MasteryLevel.BEGINNER;
        } else if (accuracy < 80) {
            return UserCategoryProgress.MasteryLevel.INTERMEDIATE;
        } else {
            return UserCategoryProgress.MasteryLevel.ADVANCED;
        }
    }
}
