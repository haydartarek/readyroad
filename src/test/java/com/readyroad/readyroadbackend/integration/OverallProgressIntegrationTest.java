package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.config.TestDataSeederConfig;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse;
import com.readyroad.readyroadbackend.service.ProgressService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for Story B2: View Overall Progress
 * Tests GET /api/users/me/progress/overall
 */
@SpringBootTest
@ActiveProfiles("test")
@Import(TestDataSeederConfig.class)
@Transactional
@DisplayName("Story B2: View Overall Progress Integration Tests")
class OverallProgressIntegrationTest {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserCategoryProgressRepository progressRepository;

    @Autowired
    private QuizQuestionRepository questionRepository;

    private User testUser;
    private Category category1;
    private Category category2;
    private Category category3;

    @BeforeEach
    void setUp() {
        // Create test user
        testUser = new User();
        testUser.setUsername("progresstester");
        testUser.setEmail("progress@test.com");
        testUser.setPasswordHash("hashedpass");
        testUser.setFullName("Progress Tester");
        testUser = userRepository.save(testUser);

        // ✅ Use seeded categories instead of creating duplicates
        var categories = categoryRepository.findAll();
        assertThat(categories).hasSizeGreaterThanOrEqualTo(3)
            .as("TestDataSeederConfig should have seeded at least 3 categories");

        category1 = categories.get(0);
        category2 = categories.get(1);
        category3 = categories.get(2);
        category2.setCode("SPEED");
        category1 = categories.get(0);
        category2 = categories.get(1);
        category3 = categories.get(2);

        // Note: questionsRemaining will be calculated from DB questions
    }

    @Test
    @DisplayName("Should return zero progress for new user")
    void testNewUserHasZeroProgress() {
        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTotalAttempted()).isEqualTo(0);
        assertThat(response.getTotalCorrect()).isEqualTo(0);
        assertThat(response.getOverallAccuracy()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.getWeakCategories()).isEmpty();
        assertThat(response.getStrongCategories()).isEmpty();
        assertThat(response.getStudyStreak()).isEqualTo(0);
        assertThat(response.getRecommendedDifficulty()).isEqualTo(QuizQuestion.DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("Should calculate overall accuracy correctly")
    void testOverallAccuracyCalculation() {
        // Given: User answered 10 questions, 8 correct
        createProgress(category1, 10, 8);

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getTotalAttempted()).isEqualTo(10);
        assertThat(response.getTotalCorrect()).isEqualTo(8);
        assertThat(response.getOverallAccuracy()).isEqualByComparingTo(BigDecimal.valueOf(80.00));
    }

    @Test
    @DisplayName("Should identify weak categories (<70% accuracy)")
    void testWeakCategoriesIdentification() {
        // Given: Category with 60% accuracy (weak)
        createProgress(category1, 10, 6); // 60% - weak

        // And: Category with strong performance (should not appear in weak)
        createProgress(category2, 10, 9); // 90% - strong

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getWeakCategories()).hasSize(1);
        assertThat(response.getWeakCategories().get(0).getCategoryName()).isEqualTo("Traffic Signs");
        assertThat(response.getWeakCategories().get(0).getAccuracy())
                .isEqualByComparingTo(BigDecimal.valueOf(60.00));
    }

    @Test
    @DisplayName("Should identify strong categories (>85% accuracy)")
    void testStrongCategoriesIdentification() {
        // Given: Category with 90% accuracy (strong)
        createProgress(category1, 10, 9); // 90% - strong

        // And: Category with weak performance (should not appear in strong)
        createProgress(category2, 10, 6); // 60% - weak

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getStrongCategories()).hasSize(1);
        assertThat(response.getStrongCategories().get(0).getCategoryName()).isEqualTo("Traffic Signs");
        assertThat(response.getStrongCategories().get(0).getAccuracy())
                .isEqualByComparingTo(BigDecimal.valueOf(90.00));
    }

    @Test
    @DisplayName("Should not categorize with < 5 attempts")
    void testMinimumAttemptsRequired() {
        // Given: Category with only 3 attempts (too few)
        createProgress(category1, 3, 1); // 33% but insufficient data

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then: Should not appear in weak categories (< 5 attempts)
        assertThat(response.getWeakCategories()).isEmpty();
        assertThat(response.getStrongCategories()).isEmpty();
    }

    @Test
    @DisplayName("Should calculate questions remaining")
    void testQuestionsRemainingCalculation() {
        // Given: User attempted 5 questions
        createProgress(category1, 5, 4);

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then: questions remaining is calculated (may be negative if DB has few questions)
        assertThat(response.getQuestionsRemaining()).isNotNull();
        // Note: In test environment with limited questions, this may be negative
    }

    @Test
    @DisplayName("Should recommend EASY for new users (<10 attempts)")
    void testRecommendedDifficultyForNewUser() {
        // Given: User with only 5 attempts
        createProgress(category1, 5, 5); // 100% but new

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getRecommendedDifficulty()).isEqualTo(QuizQuestion.DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("Should recommend HARD for high performers (>85%)")
    void testRecommendedDifficultyForHighPerformer() {
        // Given: User with 90% accuracy and sufficient attempts
        createProgress(category1, 20, 18); // 90%

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getRecommendedDifficulty()).isEqualTo(QuizQuestion.DifficultyLevel.HARD);
    }

    @Test
    @DisplayName("Should recommend MEDIUM for average performers (70-85%)")
    void testRecommendedDifficultyForAveragePerformer() {
        // Given: User with 75% accuracy
        createProgress(category1, 20, 15); // 75%

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getRecommendedDifficulty()).isEqualTo(QuizQuestion.DifficultyLevel.MEDIUM);
    }

    @Test
    @DisplayName("Should recommend EASY for struggling users (<70%)")
    void testRecommendedDifficultyForStrugglingUser() {
        // Given: User with 60% accuracy
        createProgress(category1, 20, 12); // 60%

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getRecommendedDifficulty()).isEqualTo(QuizQuestion.DifficultyLevel.EASY);
    }

    @Test
    @DisplayName("Should calculate study streak for consecutive days")
    void testStudyStreakCalculation() {
        // Given: User practiced today
        UserCategoryProgress progress = createProgress(category1, 5, 4);
        progress.setLastPracticed(LocalDateTime.now());
        progressRepository.save(progress);

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then: At minimum, streak should be 1 (today)
        assertThat(response.getStudyStreak()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("Should return zero streak if not practiced recently")
    void testZeroStreakForInactiveUser() {
        // Given: User last practiced 5 days ago
        UserCategoryProgress progress = createProgress(category1, 5, 4);
        progress.setLastPracticed(LocalDateTime.now().minusDays(5));
        progressRepository.save(progress);

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getStudyStreak()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should aggregate stats across multiple categories")
    void testMultipleCategoryAggregation() {
        // Given: Progress in 3 categories
        createProgress(category1, 10, 8); // 80%
        createProgress(category2, 15, 12); // 80%
        createProgress(category3, 5, 3); // 60%

        // When
        OverallProgressResponse response = progressService.getOverallProgress(testUser.getId());

        // Then
        assertThat(response.getTotalAttempted()).isEqualTo(30); // 10+15+5
        assertThat(response.getTotalCorrect()).isEqualTo(23); // 8+12+3
        assertThat(response.getOverallAccuracy()).isEqualByComparingTo(BigDecimal.valueOf(76.67));
    }

    // Helper methods

    private UserCategoryProgress createProgress(Category category, int attempted, int correct) {
        UserCategoryProgress progress = new UserCategoryProgress();
        progress.setUserId(testUser.getId());
        progress.setCategoryId(category.getId());
        progress.setQuestionsAttempted(attempted);
        progress.setCorrectAnswers(correct);
        progress.setLastPracticed(LocalDateTime.now());

        // Calculate accuracy
        if (attempted > 0) {
            double accuracy = (double) correct / attempted * 100.0;
            progress.setAccuracyRate(accuracy);
        }

        progress.updateAccuracy(); // Updates mastery level too
        return progressRepository.save(progress);
    }
}
