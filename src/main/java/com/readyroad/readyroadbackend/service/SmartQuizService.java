package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        // Guest mode: no cooldown tracking (userId = null)
        if (userId == null) {
            log.debug("Guest mode: no cooldown tracking");
            recentQuestionIds = List.of(); // Empty list = no questions to filter
        } else {
            // Authenticated mode: enforce 24h cooldown
            LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
            recentQuestionIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);
            log.debug("User {} has seen {} questions in last 24h", userId, recentQuestionIds.size());
        }

        int fetchCount = Math.min(count * MAX_FETCH_MULTIPLIER, 150);
        List<QuizQuestion> candidates = fetchCandidateQuestions(categoryId, fetchCount);

        // Base filter: active + published + valid options (always applied)
        List<QuizQuestion> compliantCandidates = candidates.stream()
                .filter(q -> q != null && q.getId() != null)
                .filter(q -> Boolean.TRUE.equals(q.getIsActive())) // belt-and-suspenders
                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) // Belgian invariant
                .filter(this::hasValidOptions) // quality gate
                .collect(Collectors.toList());

        // Phase 1: Try fresh (not seen in 24h) questions first
        List<QuizQuestion> freshQuestions = compliantCandidates.stream()
                .filter(q -> !recentQuestionIds.contains(q.getId()))
                .limit(count)
                .collect(Collectors.toList());

        log.info("Found {} fresh questions out of {} compliant candidates", freshQuestions.size(), compliantCandidates.size());

        // Phase 2: Fallback — if not enough fresh questions, include already-seen questions
        // This ensures users can ALWAYS practice, even after seeing all questions recently.
        if (freshQuestions.size() < count && !compliantCandidates.isEmpty()) {
            Set<Long> alreadySelected = freshQuestions.stream()
                    .map(QuizQuestion::getId)
                    .collect(Collectors.toSet());
            int needed = count - freshQuestions.size();

            List<QuizQuestion> fallbackQuestions = compliantCandidates.stream()
                    .filter(q -> !alreadySelected.contains(q.getId()))
                    .limit(needed)
                    .collect(Collectors.toList());

            if (!fallbackQuestions.isEmpty()) {
                log.info("Cooldown fallback: adding {} already-seen questions (user has reviewed all fresh content)",
                        fallbackQuestions.size());
                List<QuizQuestion> combined = new ArrayList<>(freshQuestions);
                combined.addAll(fallbackQuestions);
                Collections.shuffle(combined); // mix fresh and review questions
                freshQuestions = combined;
            }
        }

        // Only record history for authenticated users
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
        // Use upsert so re-visiting the same question never causes a duplicate-key error.
        // ON DUPLICATE KEY UPDATE increments times_shown and refreshes last_shown_at.
        questions.forEach(q ->
                historyRepository.upsertQuestionShown(userId, q.getId(), now, shownType));
        log.debug("Recorded {} questions in history for user {} with type {}",
                questions.size(), userId, shownType);
    }

    // ============================================================================
    // Quality Gate: Options Validation
    // ============================================================================

    /**
     * Validates that a question has delivery-quality options:
     * - 2–3 options present (Belgian standard)
     * - Exactly 1 correct option
     * - All options have at least one non-blank language text
     *
     * Questions failing this gate are silently skipped during delivery
     * and logged at WARN level for admin diagnostics.
     */
    private boolean hasValidOptions(QuizQuestion question) {
        if (question.getOptions() == null || question.getOptions().isEmpty()) {
            log.warn("Question {} skipped: no options", question.getId());
            return false;
        }

        int optionCount = question.getOptions().size();
        if (optionCount < 2 || optionCount > 3) {
            log.warn("Question {} skipped: {} options (need 2-3)", question.getId(), optionCount);
            return false;
        }

        long correctCount = question.getOptions().stream()
                .filter(o -> Boolean.TRUE.equals(o.getIsCorrect()))
                .count();
        if (correctCount != 1) {
            log.warn("Question {} skipped: {} correct options (need exactly 1)", question.getId(), correctCount);
            return false;
        }

        boolean allHaveText = question.getOptions().stream()
                .allMatch(o -> hasAnyText(o.getOptionTextEn(), o.getOptionTextAr(),
                        o.getOptionTextNl(), o.getOptionTextFr()));
        if (!allHaveText) {
            log.warn("Question {} skipped: option(s) missing all language text", question.getId());
            return false;
        }

        return true;
    }

    private boolean hasAnyText(String... texts) {
        for (String t : texts) {
            if (t != null && !t.isBlank())
                return true;
        }
        return false;
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

        // Adaptive quiz detailed logging (debug level for production cleanliness)
        log.debug("ADAPTIVE QUIZ GENERATION: userId={}, accuracy={}%, recommended={}",
                userId, String.format("%.1f", accuracy * 100), recommendedLevel);

        // Step 2: Get recent question IDs (24h cooldown - Law #1)
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        List<Long> recentQuestionIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);

        log.debug("User {} has seen {} questions in last 24h", userId, recentQuestionIds.size());

        // Step 3: Fetch candidates with difficulty bias
        int fetchCount = Math.min(count * MAX_FETCH_MULTIPLIER, 150);
        List<QuizQuestion> candidates = fetchCandidateQuestionsWithDifficulty(categoryId, fetchCount, recommendedLevel);

        // Step 4: Filter by cooldown, PUBLISHED status, quality gate, and limit count
        List<QuizQuestion> freshQuestions = candidates.stream()
                .filter(q -> q != null)
                .filter(q -> q.getId() != null) // Null-safe: prevent NPE in contains()
                .filter(q -> Boolean.TRUE.equals(q.getIsActive())) // belt-and-suspenders
                .filter(q -> q.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED) // Belgian invariant
                .filter(this::hasValidOptions) // quality gate
                .filter(q -> !recentQuestionIds.contains(q.getId()))
                .limit(count)
                .collect(Collectors.toList());

        // RESULTS LOGGING - DISTRIBUTION ANALYSIS
        Map<QuizQuestion.DifficultyLevel, Long> distribution = freshQuestions.stream()
                .collect(Collectors.groupingBy(
                        QuizQuestion::getDifficultyLevel,
                        Collectors.counting()));

        log.debug("  Quiz Distribution: EASY={}, MEDIUM={}, HARD={}",
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.EASY, 0L),
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.MEDIUM, 0L),
                distribution.getOrDefault(QuizQuestion.DifficultyLevel.HARD, 0L));

        log.debug("Adaptive quiz: found {} fresh questions (difficulty bias: {})",
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
    // Statistics Methods — Compliant Pool Only
    // ============================================================================

    /**
     * Count delivery-compliant questions: active, PUBLISHED, 2-3 options, 1
     * correct.
     * Used for guest/public stats (no userId required).
     */
    public long countTotalQuestions() {
        return quizQuestionRepository.countCompliantQuestions();
    }

    /**
     * Count fresh compliant questions available for a specific user (24h cooldown
     * enforced).
     */
    public long countFreshQuestions(Long userId) {
        long totalCompliant = quizQuestionRepository.countCompliantQuestions();
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        long recentCount = historyRepository.countByUserIdAndLastShownAtAfter(userId, cooldownThreshold);
        return Math.max(0, totalCompliant - recentCount);
    }

    /**
     * Count fresh compliant questions in a specific category for a user (24h
     * cooldown enforced).
     */
    public long countFreshQuestionsInCategory(Long userId, Long categoryId) {
        long totalInCategory = quizQuestionRepository.countCompliantQuestionsByCategory(categoryId);
        LocalDateTime cooldownThreshold = LocalDateTime.now().minusHours(COOLDOWN_HOURS);
        List<Long> recentIds = historyRepository.findRecentQuestionIdsByUserId(userId, cooldownThreshold);

        if (recentIds.isEmpty()) {
            return totalInCategory;
        }

        long recentInCategory = quizQuestionRepository.countByIdInAndCategoryId(recentIds, categoryId);
        return Math.max(0, totalInCategory - recentInCategory);
    }

    /**
     * Count delivery-compliant questions in a specific category.
     * Used for guest/public stats (no userId required).
     */
    public long countTotalQuestionsInCategory(Long categoryId) {
        return quizQuestionRepository.countCompliantQuestionsByCategory(categoryId);
    }
}
