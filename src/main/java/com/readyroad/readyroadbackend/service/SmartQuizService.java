package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.model.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Smart Quiz Service
 * Phase 3: Implements 24h cooldown (Law #1)
 * Phase 4: Implements adaptive difficulty (Law #2)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SmartQuizService {

    private final QuizQuestionRepository quizQuestionRepository;
    private final UserQuestionHistoryRepository historyRepository;
    private final UserPerformanceService performanceService; // Phase 4

    private static final int COOLDOWN_HOURS = 24;
    private static final int MAX_FETCH_MULTIPLIER = 3;

    /**
     * Generate smart quiz with 24h cooldown enforcement.
     * 
     * @param userId User ID (null for guest access without cooldown)
     * @param count  Number of questions to generate
     * @return List of quiz questions
     */
    @Transactional
    public List<QuizQuestion> generateSmartQuiz(Long userId, int count) {
        return generateSmartQuiz(userId, count, null);
    }

    /**
     * Generate smart quiz by category with 24h cooldown enforcement.
     * 
     * @param userId     User ID (null for guest access without cooldown tracking)
     * @param count      Number of questions to generate
     * @param categoryId Category ID (null for random questions from all categories)
     * @return List of quiz questions
     */
    @Transactional
    public List<QuizQuestion> generateSmartQuiz(Long userId, int count, Long categoryId) {
        if (count <= 0 || count > 50) {
            throw new IllegalArgumentException("Count must be between 1 and 50");
        }

        log.info("Generating smart quiz: userId={}, count={}, categoryId={}", userId, count, categoryId);

        List<Long> recentQuestionIds;

        // ✅ Guest mode: no cooldown tracking (userId = null)
        if (userId == null) {
            log.debug("Guest mode: no cooldown tracking");
            recentQuestionIds = List.of(); // Empty list = no questions to filter
        } else {
            // 🔐 Authenticated mode: enforce 24h cooldown
            LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
            recentQuestionIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);
            log.debug("User {} has seen {} questions in last 24h", userId, recentQuestionIds.size());
        }

        int fetchCount = Math.min(count * MAX_FETCH_MULTIPLIER, 150);
        List<QuizQuestion> candidates = fetchCandidateQuestions(categoryId, fetchCount);

        List<QuizQuestion> freshQuestions = candidates.stream()
                .filter(q -> !recentQuestionIds.contains(q.getId()))
                .limit(count)
                .collect(Collectors.toList());

        log.info("Found {} fresh questions out of {} candidates", freshQuestions.size(), candidates.size());

        // ✅ Only record history for authenticated users
        if (userId != null && !freshQuestions.isEmpty()) {
            String contextType = (categoryId != null) ? "CATEGORY" : "RANDOM";
            recordQuestionHistory(userId, freshQuestions, contextType);
        }

        return freshQuestions;
    }

    private List<QuizQuestion> fetchCandidateQuestions(Long categoryId, int fetchCount) {
        List<QuizQuestion> questions;
        if (categoryId != null) {
            questions = quizQuestionRepository.findRandomQuestionsByCategoryWithOptions(
                    categoryId, PageRequest.of(0, fetchCount));
        } else {
            questions = quizQuestionRepository.findRandomQuestionsWithOptions(
                    PageRequest.of(0, fetchCount));
        }

        // Shuffle to randomize order (since ORDER BY RAND() doesn't work in H2)
        Collections.shuffle(questions);
        return questions;
    }

    /**
     * Records that questions were shown to user (not answered yet).
     * Sets lastShownAt and lastShownType for cooldown tracking.
     * Does NOT set answeredAt (that's only for when user submits answer).
     */
    private void recordQuestionHistory(Long userId, List<QuizQuestion> questions) {
        recordQuestionHistory(userId, questions, "RANDOM");
    }

    /**
     * Records that questions were shown to user with specific context type.
     * 
     * @param userId    User ID
     * @param questions Questions that were shown
     * @param shownType Context type: "RANDOM", "CATEGORY", "EXAM", "SMART_QUIZ"
     */
    private void recordQuestionHistory(Long userId, List<QuizQuestion> questions, String shownType) {
        LocalDateTime now = LocalDateTime.now();
        List<UserQuestionHistory> historyRecords = questions.stream()
                .map(q -> UserQuestionHistory.builder()
                        .userId(userId)
                        .questionId(q.getId())
                        .lastShownAt(now)
                        .lastShownType(shownType)
                        .timesShown(1)
                        // ❌ DON'T set answeredAt here - only when user submits answer
                        .build())
                .collect(Collectors.toList());

        historyRepository.saveAll(historyRecords);
        log.debug("Recorded {} questions in history for user {} with type {}",
                historyRecords.size(), userId, shownType);
    }

    // ============================================================================
    // Phase 4: Adaptive Difficulty (Law #2)
    // ============================================================================

    /**
     * Generate adaptive quiz based on user performance.
     * Combines Law #1 (24h cooldown) + Law #2 (adaptive difficulty).
     *
     * Algorithm:
     * 1. Calculate user accuracy from recent history
     * 2. Determine recommended difficulty level
     * 3. Fetch questions biased toward that difficulty
     * 4. Apply 24h cooldown filter
     * 5. Record history for future cooldown
     *
     * @param userId     User ID
     * @param count      Number of questions requested
     * @param categoryId Optional category filter
     * @return List of questions matching user level (may be fewer than requested)
     */
    @Transactional
    public List<QuizQuestion> generateAdaptiveQuiz(Long userId, int count, Long categoryId) {
        if (count <= 0 || count > 50) {
            throw new IllegalArgumentException("Count must be between 1 and 50");
        }

        log.info("Generating ADAPTIVE quiz: userId={}, count={}, categoryId={}", userId, count, categoryId);

        // Step 1: Get user performance
        double accuracy = performanceService.calculateRecentAccuracy(userId);
        QuizQuestion.DifficultyLevel recommendedLevel = performanceService.getRecommendedDifficulty(userId);

        // 🎯 DETAILED LOGGING FOR ACADEMIC DEFENSE
        log.info("🎯 ADAPTIVE QUIZ GENERATION:");
        log.info("  User ID: {}", userId);
        log.info("  Recent Accuracy: {}%", String.format("%.1f", accuracy * 100));
        log.info("  Recommended Difficulty: {}", recommendedLevel);

        // Step 2: Get recent question IDs (24h cooldown - Law #1)
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        List<Long> recentQuestionIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);

        log.debug("User {} has seen {} questions in last 24h", userId, recentQuestionIds.size());

        // Step 3: Fetch candidates with difficulty bias
        int fetchCount = Math.min(count * MAX_FETCH_MULTIPLIER, 150);
        List<QuizQuestion> candidates = fetchCandidateQuestionsWithDifficulty(categoryId, fetchCount, recommendedLevel);

        // Step 4: Filter by cooldown, PUBLISHED status, and limit count
        List<QuizQuestion> freshQuestions = candidates.stream()
                .filter(q -> q != null)
                .filter(q -> q.getId() != null) // ✅ Null-safe: prevent NPE in contains()
                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) // ✅ Belgian invariant
                .filter(q -> !recentQuestionIds.contains(q.getId()))
                .limit(count)
                .collect(Collectors.toList());

        // 📊 RESULTS LOGGING - DISTRIBUTION ANALYSIS
        Map<QuizQuestion.DifficultyLevel, Long> distribution = freshQuestions.stream()
                .collect(Collectors.groupingBy(
                        QuizQuestion::getDifficultyLevel,
                        Collectors.counting()));

        log.info("  Quiz Distribution: EASY={}, MEDIUM={}, HARD={}",
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.EASY, 0L),
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.MEDIUM, 0L),
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.HARD, 0L));

        log.info("Adaptive quiz: found {} fresh questions (difficulty bias: {})",
                freshQuestions.size(), recommendedLevel);

        // Step 5: Record history as SMART_QUIZ type
        if (!freshQuestions.isEmpty()) {
            recordQuestionHistory(userId, freshQuestions, "SMART_QUIZ");
        }

        return freshQuestions;
    }

    /**
     * Fetch candidate questions with difficulty bias.
     * Tries to get questions matching recommended level first,
     * falls back to all difficulties if needed.
     */
    private List<QuizQuestion> fetchCandidateQuestionsWithDifficulty(
            Long categoryId,
            int fetchCount,
            QuizQuestion.DifficultyLevel recommendedLevel) {
        List<QuizQuestion> candidates;

        if (categoryId != null) {
            // Try recommended difficulty in category
            candidates = quizQuestionRepository.findByCategoryAndDifficultyRandom(
                    categoryId, recommendedLevel, PageRequest.of(0, fetchCount));

            // If not enough, get any difficulty in category
            if (candidates.size() < fetchCount / 2) {
                log.debug("Not enough {} questions in category {}, fetching mixed",
                        recommendedLevel, categoryId);
                candidates = quizQuestionRepository.findRandomQuestionsByCategoryWithOptions(
                        categoryId, PageRequest.of(0, fetchCount));
            }
        } else {
            // Try recommended difficulty globally
            candidates = quizQuestionRepository.findByDifficultyRandom(
                    recommendedLevel, PageRequest.of(0, fetchCount));

            // If not enough, get any difficulty
            if (candidates.size() < fetchCount / 2) {
                log.debug("Not enough {} questions globally, fetching mixed", recommendedLevel);
                candidates = quizQuestionRepository.findRandomQuestionsWithOptions(
                        PageRequest.of(0, fetchCount));
            }
        }

        // Shuffle to randomize order (since ORDER BY RAND() doesn't work in H2)
        Collections.shuffle(candidates);

        log.debug("Fetched {} candidates for difficulty {}", candidates.size(), recommendedLevel);
        return candidates;
    }

    // ============================================================================
    // Statistics Methods
    // ============================================================================

    /**
     * Count total PUBLISHED active questions available in the system.
     * Used for guest/public stats (no userId required).
     */
    public long countTotalQuestions() {
        return quizQuestionRepository.countByIsActiveTrue();
    }

    /**
     * Count fresh questions available for a specific user (24h cooldown enforced).
     */
    public long countFreshQuestions(Long userId) {
        long totalQuestions = quizQuestionRepository.countByIsActiveTrue();
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        long recentCount = historyRepository.countByUserIdAndLastShownAtAfter(userId, cooldownThreshold);
        return Math.max(0, totalQuestions - recentCount);
    }

    /**
     * Count fresh questions in a specific category for a user (24h cooldown
     * enforced).
     */
    public long countFreshQuestionsInCategory(Long userId, Long categoryId) {
        long totalInCategory = quizQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        List<Long> recentIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);

        if (recentIds.isEmpty()) {
            return totalInCategory;
        }

        long recentInCategory = quizQuestionRepository.countByIdInAndCategoryId(recentIds, categoryId);
        return Math.max(0, totalInCategory - recentInCategory);
    }
}
