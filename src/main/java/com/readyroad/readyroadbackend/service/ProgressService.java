package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import com.readyroad.readyroadbackend.domain.entity.UserWeakArea;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserWeakAreaRepository;
import com.readyroad.readyroadbackend.dto.CategoryProgressResponse;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse.CategoryProgressSummary;
import com.readyroad.readyroadbackend.dto.OverallProgressResponse.SignWeaknessSummary;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for Story B2: View Overall Progress
 * Calculates and returns user's overall learning progress metrics
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProgressService {

        private final UserCategoryProgressRepository progressRepository;
        private final CategoryRepository categoryRepository;
        private final QuizQuestionRepository questionRepository;
        private final UserQuestionHistoryRepository historyRepository;
        private final UserLessonProgressRepository lessonProgressRepository;
        private final UserWeakAreaRepository weakAreaRepository;
        private final RoadSignRepository roadSignRepository;
        private final ExamSimulationRepository examSimulationRepository;
        private final SignPracticeSessionRepository signPracticeSessionRepository;
        private final SignExamResultRepository signExamResultRepository;
        private final SignRandomPracticeSessionRepository signRandomPracticeSessionRepository;

        private static final int TOTAL_QUESTIONS_GOAL = 500;
        private static final int MIN_ATTEMPTS_FOR_CATEGORIZATION = 5;
        private static final BigDecimal WEAK_THRESHOLD = BigDecimal.valueOf(70.00);
        private static final BigDecimal STRONG_THRESHOLD = BigDecimal.valueOf(85.00);
        private static final int MIN_ATTEMPTS_FOR_DIFFICULTY = 10;
        private static final int MIN_ATTEMPTS_FOR_WEAK_SIGN = 2;
        private static final BigDecimal WEAK_SIGN_THRESHOLD = BigDecimal.valueOf(80.00);
        private static final int MAX_WEAK_SIGNS = 5;
        private static final double EXAM_PASS_THRESHOLD = 82.0;

        /**
         * Get overall progress for a user
         *
         * @param userId The user ID
         * @return Overall progress response with statistics
         */
        @Transactional(readOnly = true)
        public OverallProgressResponse getOverallProgress(Long userId) {
                log.info("Getting overall progress for user {}", userId);

                // Get all progress records for the user
                List<UserCategoryProgress> allProgressRecords = progressRepository.findByUserId(userId);
                List<UserCategoryProgress> progressRecords = allProgressRecords.stream()
                                .filter(this::isActiveTheoreticalProgress)
                                .toList();
                List<UserLessonProgress> lessonProgressRecords = lessonProgressRepository.findAllByUserId(userId);

                // Calculate aggregated statistics
                int totalAttempted = progressRecords.stream()
                                .mapToInt(UserCategoryProgress::getQuestionsAttempted)
                                .sum();

                int totalCorrect = progressRecords.stream()
                                .mapToInt(UserCategoryProgress::getCorrectAnswers)
                                .sum();

                BigDecimal overallAccuracy = calculateOverallAccuracy(totalAttempted, totalCorrect);

                // Get category map for names and codes
                Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
                                .collect(Collectors.toMap(Category::getId, c -> c));

                // Identify weak and strong categories
                List<CategoryProgressSummary> weakCategories = identifyWeakCategories(progressRecords, categoryMap);
                List<CategoryProgressSummary> strongCategories = identifyStrongCategories(progressRecords, categoryMap);

                // Most-studied categories (top 3 by questionsAttempted)
                List<CategoryProgressSummary> mostStudiedCategories = computeMostStudiedCategories(progressRecords,
                                categoryMap);

                // Calculate remaining questions toward the 500-question goal
                int questionsRemaining = Math.max(0, TOTAL_QUESTIONS_GOAL - totalAttempted);

                int lessonsStartedCount = (int) lessonProgressRecords.stream()
                                .filter(progress -> progress.getPagesRead() > 0
                                                || !"NOT_STARTED".equals(progress.getStatus()))
                                .count();
                int lessonsCompletedCount = (int) lessonProgressRecords.stream()
                                .filter(progress -> "COMPLETED".equals(progress.getStatus()))
                                .count();

                int activeTheoryExamCount = (int) examSimulationRepository.countByUserIdAndStatus(
                                userId, ExamSimulation.ExamStatus.IN_PROGRESS);
                int incompleteSignPracticeCount = (int) signPracticeSessionRepository
                                .countByUserIdAndStatus(userId, SignPracticeSession.SessionStatus.IN_PROGRESS);
                int activeRandomSignExamCount = (int) signRandomPracticeSessionRepository.countByUser_IdAndStatus(
                                userId, SignRandomPracticeSession.SessionStatus.IN_PROGRESS);
                int incompleteActivitiesCount = activeTheoryExamCount + incompleteSignPracticeCount
                                + activeRandomSignExamCount;

                // Real consecutive-day study streak using all tracked learning activity
                int studyStreak = calculateStudyStreak(userId, allProgressRecords, lessonProgressRecords);

                // Date of the most recent tracked learning activity
                String lastActivityDate = findLastActivityDate(userId, allProgressRecords, lessonProgressRecords);

                // Recommend difficulty
                QuizQuestion.DifficultyLevel recommendedDifficulty = recommendDifficulty(totalAttempted,
                                overallAccuracy);

                // Exam simulation statistics
                int totalExamsTaken = (int) examSimulationRepository.countByUserIdAndStatus(
                                userId, ExamSimulation.ExamStatus.COMPLETED);
                int passedExams = (int) examSimulationRepository
                                .countByUserIdAndStatusAndScorePercentageGreaterThanEqual(
                                                userId, ExamSimulation.ExamStatus.COMPLETED, EXAM_PASS_THRESHOLD);
                int failedExams = totalExamsTaken - passedExams;
                BigDecimal passRate = totalExamsTaken > 0
                                ? BigDecimal.valueOf(passedExams * 100.0 / totalExamsTaken).setScale(2,
                                                RoundingMode.HALF_UP)
                                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);

                // Sign quiz activity statistics
                int signPracticeCount = (int) signPracticeSessionRepository
                                .countByUserIdAndStatus(userId, SignPracticeSession.SessionStatus.COMPLETED);
                int signExamCount = (int) signExamResultRepository.countByUserId(userId);
                int signPassedCount = (int) signExamResultRepository.countDistinctSignsWithPassedExam(userId);
                int signRandomExamCount = (int) signRandomPracticeSessionRepository.countByUser_IdAndStatus(
                                userId, SignRandomPracticeSession.SessionStatus.COMPLETED);
                int signRandomExamPassedCount = (int) signRandomPracticeSessionRepository
                                .countByUserIdAndPassedTrue(userId);
                List<SignWeaknessSummary> weakSigns = identifyWeakSigns(userId);

                log.info(
                                "User {} progress: attempted={}, correct={}, accuracy={}%, streak={}, lastActivity={}, exams={}, passed={}, signPractice={}, signExams={}, randomSignExams={}",
                                userId, totalAttempted, totalCorrect, overallAccuracy, studyStreak, lastActivityDate,
                                totalExamsTaken,
                                passedExams, signPracticeCount, signExamCount, signRandomExamCount);

                return OverallProgressResponse.builder()
                                .totalAttempted(totalAttempted)
                                .totalCorrect(totalCorrect)
                                .overallAccuracy(overallAccuracy)
                                .weakCategories(weakCategories)
                                .strongCategories(strongCategories)
                                .mostStudiedCategories(mostStudiedCategories)
                                .questionsRemaining(questionsRemaining)
                                .studyStreak(studyStreak)
                                .lastActivityDate(lastActivityDate)
                                .recommendedDifficulty(recommendedDifficulty)
                                .totalExamsTaken(totalExamsTaken)
                                .passedExams(passedExams)
                                .failedExams(failedExams)
                                .passRate(passRate)
                                .signPracticeCount(signPracticeCount)
                                .signExamCount(signExamCount)
                                .signPassedCount(signPassedCount)
                                .signRandomExamCount(signRandomExamCount)
                                .signRandomExamPassedCount(signRandomExamPassedCount)
                                .lessonsStartedCount(lessonsStartedCount)
                                .lessonsCompletedCount(lessonsCompletedCount)
                                .incompleteActivitiesCount(incompleteActivitiesCount)
                                .activeTheoryExamCount(activeTheoryExamCount)
                                .incompleteSignPracticeCount(incompleteSignPracticeCount)
                                .activeRandomSignExamCount(activeRandomSignExamCount)
                                .weakSigns(weakSigns)
                                .build();
        }

        /**
         * Identify weakest sign-level entries from user_weak_areas.
         *
         * Rules:
         * - sign row only (trafficSignCode is present)
         * - at least 2 answered questions for signal quality
         * - below 80% accuracy
         * - sorted by lowest accuracy then highest attempts
         */
        private List<SignWeaknessSummary> identifyWeakSigns(Long userId) {
                List<UserWeakArea> weakAreas = weakAreaRepository.findAllByUserId(userId).stream()
                                .filter(area -> area.getTrafficSignCode() != null
                                                && !area.getTrafficSignCode().isBlank())
                                .filter(area -> (area.getTotalQuestions() != null ? area.getTotalQuestions()
                                                : 0) >= MIN_ATTEMPTS_FOR_WEAK_SIGN)
                                .filter(area -> {
                                        BigDecimal accuracy = BigDecimal.valueOf(
                                                        area.getAccuracyPercentage() != null
                                                                        ? area.getAccuracyPercentage()
                                                                        : 0.0);
                                        return accuracy.compareTo(BigDecimal.ZERO) > 0
                                                        && accuracy.compareTo(WEAK_SIGN_THRESHOLD) < 0;
                                })
                                .sorted(Comparator
                                                .comparing((UserWeakArea area) -> area.getAccuracyPercentage() != null
                                                                ? area.getAccuracyPercentage()
                                                                : 0.0)
                                                .thenComparing(area -> area.getTotalQuestions() != null
                                                                ? area.getTotalQuestions()
                                                                : 0,
                                                                Comparator.reverseOrder()))
                                .limit(MAX_WEAK_SIGNS)
                                .collect(Collectors.toList());

                if (weakAreas.isEmpty()) {
                        return java.util.Collections.emptyList();
                }

                // Batch-load all needed signs in one query — avoids N+1 (up to 5 per progress
                // load)
                Set<String> codes = weakAreas.stream()
                                .map(UserWeakArea::getTrafficSignCode)
                                .collect(Collectors.toSet());
                Set<String> upperCodes = codes.stream()
                                .map(c -> c.toUpperCase(Locale.ROOT))
                                .collect(Collectors.toSet());
                codes.addAll(upperCodes);

                Map<String, RoadSign> signByCode = roadSignRepository.findBySignCodeIn(codes).stream()
                                .collect(Collectors.toMap(RoadSign::getSignCode, s -> s, (a, b) -> a));

                return weakAreas.stream()
                                .map(area -> toSignWeaknessSummary(area, signByCode))
                                .collect(Collectors.toList());
        }

        private SignWeaknessSummary toSignWeaknessSummary(UserWeakArea area, Map<String, RoadSign> signByCode) {
                String signCode = area.getTrafficSignCode();
                RoadSign sign = signByCode.get(signCode);
                if (sign == null) {
                        sign = signByCode.get(signCode.toUpperCase(Locale.ROOT));
                }
                double rawAccuracy = area.getAccuracyPercentage() != null ? area.getAccuracyPercentage() : 0.0;

                return SignWeaknessSummary.builder()
                                .signCode(signCode)
                                .signNameEn(sign != null ? sign.getNameEn() : signCode)
                                .signNameNl(sign != null ? sign.getNameNl() : null)
                                .signNameFr(sign != null ? sign.getNameFr() : null)
                                .signNameAr(sign != null ? sign.getNameAr() : null)
                                .accuracy(BigDecimal.valueOf(rawAccuracy).setScale(2, RoundingMode.HALF_UP))
                                .attempted(area.getTotalQuestions() != null ? area.getTotalQuestions() : 0)
                                .wrongAnswers(area.getWrongAnswers() != null ? area.getWrongAnswers() : 0)
                                .build();
        }

        /**
         * Calculate overall accuracy percentage
         */
        private BigDecimal calculateOverallAccuracy(int totalAttempted, int totalCorrect) {
                if (totalAttempted == 0) {
                        return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
                }

                return BigDecimal.valueOf(totalCorrect)
                                .multiply(BigDecimal.valueOf(100))
                                .divide(BigDecimal.valueOf(totalAttempted), 2, RoundingMode.HALF_UP);
        }

        /**
         * Identify weak categories (<70% accuracy, ≥5 attempts)
         * Sorted by lowest accuracy first
         */
        private List<CategoryProgressSummary> identifyWeakCategories(
                        List<UserCategoryProgress> progressRecords,
                        Map<Long, Category> categoryMap) {

                return progressRecords.stream()
                                .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_CATEGORIZATION)
                                .filter(p -> {
                                        BigDecimal accuracy = p.getAccuracyRate();
                                        return accuracy.compareTo(WEAK_THRESHOLD) < 0;
                                })
                                .map(p -> buildCategorySummary(p, categoryMap))
                                .sorted(Comparator.comparing(CategoryProgressSummary::getAccuracy))
                                .collect(Collectors.toList());
        }

        /**
         * Identify strong categories (>85% accuracy, ≥5 attempts)
         * Sorted by highest accuracy first
         */
        private List<CategoryProgressSummary> identifyStrongCategories(
                        List<UserCategoryProgress> progressRecords,
                        Map<Long, Category> categoryMap) {

                return progressRecords.stream()
                                .filter(p -> p.getQuestionsAttempted() >= MIN_ATTEMPTS_FOR_CATEGORIZATION)
                                .filter(p -> {
                                        BigDecimal accuracy = p.getAccuracyRate();
                                        return accuracy.compareTo(STRONG_THRESHOLD) > 0;
                                })
                                .map(p -> buildCategorySummary(p, categoryMap))
                                .sorted(Comparator.comparing(CategoryProgressSummary::getAccuracy).reversed())
                                .collect(Collectors.toList());
        }

        /**
         * Compute top-3 most-studied categories by questionsAttempted (descending).
         * Used by the dashboard "Most Studied" widget.
         */
        private List<CategoryProgressSummary> computeMostStudiedCategories(
                        List<UserCategoryProgress> progressRecords,
                        Map<Long, Category> categoryMap) {

                return progressRecords.stream()
                                .filter(p -> p.getQuestionsAttempted() > 0)
                                .sorted(Comparator.comparing(UserCategoryProgress::getQuestionsAttempted).reversed())
                                .limit(3)
                                .map(p -> buildCategorySummary(p, categoryMap))
                                .collect(Collectors.toList());
        }

        /**
         * Build category progress summary (used by weak, strong, and mostStudied lists)
         */
        private CategoryProgressSummary buildCategorySummary(
                        UserCategoryProgress progress,
                        Map<Long, Category> categoryMap) {

                Category category = categoryMap.get(progress.getCategoryId());
                String categoryName = (category != null) ? category.getNameEn() : "Unknown Category";
                String categoryCode = (category != null) ? category.getCode() : null;

                return CategoryProgressSummary.builder()
                                .categoryName(categoryName)
                                .categoryNameEn(category != null ? category.getNameEn() : null)
                                .categoryNameNl(category != null ? category.getNameNl() : null)
                                .categoryNameFr(category != null ? category.getNameFr() : null)
                                .categoryNameAr(category != null ? category.getNameAr() : null)
                                .categoryCode(categoryCode)
                                .accuracy(progress.getAccuracyRate().setScale(2, RoundingMode.HALF_UP))
                                .attempted(progress.getQuestionsAttempted())
                                .build();
        }

        /**
         * Calculate real consecutive-day study streak.
         *
         * Algorithm:
         * 1. Load distinct answered dates from user_question_history (descending).
         * 2. If the most recent date is neither today nor yesterday → streak = 0
         * (broken).
         * 3. Walk backwards through dates; count consecutive days that differ by
         * exactly 1.
         *
         * @param userId User ID
         * @return Number of consecutive study days (0 if user never practiced or streak
         *         broken)
         */
        private int calculateStudyStreak(Long userId,
                        List<UserCategoryProgress> categoryProgressRecords,
                        List<UserLessonProgress> lessonProgressRecords) {
                List<LocalDate> dates = collectActivityDates(userId, categoryProgressRecords, lessonProgressRecords)
                                .stream()
                                .sorted(Comparator.reverseOrder())
                                .collect(Collectors.toList());

                if (dates.isEmpty())
                        return 0;

                LocalDate today = LocalDate.now();
                LocalDate yesterday = today.minusDays(1);
                LocalDate mostRecent = dates.get(0);

                // Streak is broken if last practice was before yesterday
                if (!mostRecent.equals(today) && !mostRecent.equals(yesterday)) {
                        log.debug("Streak broken for user {}: last practice={}", userId, mostRecent);
                        return 0;
                }

                // Count consecutive days starting from the most-recent date
                int streak = 1;
                for (int i = 0; i < dates.size() - 1; i++) {
                        LocalDate current = dates.get(i);
                        LocalDate next = dates.get(i + 1);
                        if (current.minusDays(1).equals(next)) {
                                streak++;
                        } else {
                                break; // Gap found – stop counting
                        }
                }

                log.debug("Study streak for user {}: {} day(s)", userId, streak);
                return streak;
        }

        private String findLastActivityDate(Long userId,
                        List<UserCategoryProgress> categoryProgressRecords,
                        List<UserLessonProgress> lessonProgressRecords) {
                return collectActivityDates(userId, categoryProgressRecords, lessonProgressRecords).stream()
                                .max(LocalDate::compareTo)
                                .map(LocalDate::toString)
                                .orElse(null);
        }

        private Set<LocalDate> collectActivityDates(Long userId,
                        List<UserCategoryProgress> categoryProgressRecords,
                        List<UserLessonProgress> lessonProgressRecords) {
                Set<LocalDate> dates = new HashSet<>();

                List<LocalDate> answeredDates = historyRepository.findDistinctAnswerDatesByUserId(userId);
                if (answeredDates != null) {
                        answeredDates.stream()
                                        .filter(date -> date != null)
                                        .forEach(dates::add);
                }

                categoryProgressRecords.forEach(progress -> addDate(dates, progress.getLastPracticed()));

                signPracticeSessionRepository.findAllByUserIdOrderByStartedAtDesc(userId)
                                .stream()
                                .filter(session -> session.getStatus() == SignPracticeSession.SessionStatus.COMPLETED)
                                .forEach(session -> {
                                        addDate(dates, session.getStartedAt());
                                        addDate(dates, session.getCompletedAt());
                                });

                signRandomPracticeSessionRepository.findAllByUserIdOrderByStartedAtDesc(userId)
                                .stream()
                                .filter(session -> session.getStatus() == SignRandomPracticeSession.SessionStatus.COMPLETED)
                                .forEach(session -> {
                                        addDate(dates, session.getStartedAt());
                                        addDate(dates, session.getCompletedAt());
                                });

                examSimulationRepository.findByUserIdOrderByStartedAtDesc(userId)
                                .stream()
                                .filter(ExamSimulation::isCompleted)
                                .forEach(exam -> {
                                        addDate(dates, exam.getStartedAt());
                                        addDate(dates, exam.getCompletedAt());
                                });

                lessonProgressRecords.forEach(progress -> {
                        addDate(dates, progress.getLastSeenAt());
                        addDate(dates, progress.getCompletedAt());
                });

                return dates;
        }

        private void addDate(Set<LocalDate> dates, LocalDateTime dateTime) {
                if (dateTime != null) {
                        dates.add(dateTime.toLocalDate());
                }
        }

        private void addDate(Set<LocalDate> dates, Instant instant) {
                if (instant != null) {
                        dates.add(instant.atZone(ZoneId.systemDefault()).toLocalDate());
                }
        }

        /**
         * Recommend difficulty level based on performance
         *
         * Rules:
         * - <10 attempts: EASY (insufficient data)
         * - ≥10 attempts, <70%: EASY (struggling)
         * - ≥10 attempts, 70-85%: MEDIUM (average)
         * - ≥10 attempts, >85%: HARD (high performer)
         */
        private QuizQuestion.DifficultyLevel recommendDifficulty(int totalAttempted, BigDecimal overallAccuracy) {
                // Insufficient data
                if (totalAttempted < MIN_ATTEMPTS_FOR_DIFFICULTY) {
                        return QuizQuestion.DifficultyLevel.EASY;
                }

                // High performer
                if (overallAccuracy.compareTo(STRONG_THRESHOLD) > 0) {
                        return QuizQuestion.DifficultyLevel.HARD;
                }

                // Struggling user
                if (overallAccuracy.compareTo(WEAK_THRESHOLD) < 0) {
                        return QuizQuestion.DifficultyLevel.EASY;
                }

                // Average performer
                return QuizQuestion.DifficultyLevel.MEDIUM;
        }

        /**
         * Get category-level progress for a user
         * Story B3: View Category-Level Progress
         *
         * @param userId The user ID
         * @return List of category progress responses
         */
        @Transactional(readOnly = true)
        public List<CategoryProgressResponse> getCategoryProgress(Long userId) {
                log.info("Getting category progress for user {}", userId);

                // Get all progress records for user
                List<UserCategoryProgress> progressRecords = progressRepository.findByUserId(userId).stream()
                                .filter(this::isActiveTheoreticalProgress)
                                .toList();

                if (progressRecords.isEmpty()) {
                        log.info("User {} has no category progress", userId);
                        return new ArrayList<>();
                }

                // Get all categories for name mapping
                Map<Long, Category> categoryMap = categoryRepository.findAll().stream()
                                .collect(Collectors.toMap(Category::getId, c -> c));

                // Pre-load compliant question counts per category in one query — avoids N+1
                Map<Long, Long> compliantByCategory = questionRepository.countCompliantQuestionsByCategoryIds().stream()
                                .collect(Collectors.toMap(
                                                row -> ((Number) row[0]).longValue(),
                                                row -> ((Number) row[1]).longValue()));

                // Convert to response DTOs
                List<CategoryProgressResponse> responses = progressRecords.stream()
                                .map(progress -> mapToCategoryProgressResponse(progress,
                                                categoryMap.get(progress.getCategoryId()),
                                                compliantByCategory.getOrDefault(progress.getCategoryId(), 0L)))
                                .collect(Collectors.toList());

                log.info("Returning {} category progress records for user {}", responses.size(), userId);
                return responses;
        }

        private boolean isActiveTheoreticalProgress(UserCategoryProgress progress) {
                Category category = progress.getCategory();
                return category != null
                                && Boolean.TRUE.equals(category.getIsActive())
                                && category.getContentScope() != null
                                && category.getContentScope().supportsTheoreticalExam();
        }

        /**
         * Map UserCategoryProgress entity to CategoryProgressResponse DTO
         */
        private CategoryProgressResponse mapToCategoryProgressResponse(
                        UserCategoryProgress progress,
                        Category category,
                        long totalInCategory) {
                BigDecimal accuracyRate = progress.getAccuracyRate()
                                .setScale(2, RoundingMode.HALF_UP);

                int questionsAttempted = progress.getQuestionsAttempted();

                // Determine if weak category (< 70% AND >= 5 attempts)
                boolean isWeak = accuracyRate.compareTo(WEAK_THRESHOLD) < 0
                                && questionsAttempted >= MIN_ATTEMPTS_FOR_CATEGORIZATION;

                // Determine if strong category (> 85% AND >= 5 attempts)
                boolean isStrong = accuracyRate.compareTo(STRONG_THRESHOLD) > 0
                                && questionsAttempted >= MIN_ATTEMPTS_FOR_CATEGORIZATION;

                // Recommend difficulty for this category
                String recommendedDifficulty = recommendCategoryDifficulty(
                                accuracyRate,
                                questionsAttempted).name();

                int questionsRemaining = (int) Math.max(0L, totalInCategory - questionsAttempted);

                return CategoryProgressResponse.builder()
                                .categoryId(progress.getCategoryId())
                                .categoryName(category != null ? category.getNameEn() : "Unknown")
                                .categoryNameEn(category != null ? category.getNameEn() : null)
                                .categoryNameNl(category != null ? category.getNameNl() : null)
                                .categoryNameFr(category != null ? category.getNameFr() : null)
                                .categoryNameAr(category != null ? category.getNameAr() : null)
                                .categoryCode(category != null ? category.getCode() : null)
                                .questionsAttempted(questionsAttempted)
                                .correctAnswers(progress.getCorrectAnswers())
                                .accuracyRate(accuracyRate)
                                .masteryLevel(progress.getMasteryLevel())
                                .lastPracticed(progress.getLastPracticed())
                                .isWeakCategory(isWeak)
                                .isStrongCategory(isStrong)
                                .questionsRemaining(questionsRemaining)
                                .recommendedDifficulty(recommendedDifficulty)
                                .build();
        }

        /**
         * Recommend difficulty level for a specific category
         */
        private QuizQuestion.DifficultyLevel recommendCategoryDifficulty(
                        BigDecimal accuracy,
                        int attempts) {
                // Not enough data
                if (attempts < MIN_ATTEMPTS_FOR_DIFFICULTY) {
                        return QuizQuestion.DifficultyLevel.EASY;
                }

                // High performer in this category
                if (accuracy.compareTo(STRONG_THRESHOLD) > 0) {
                        return QuizQuestion.DifficultyLevel.HARD;
                }

                // Struggling in this category
                if (accuracy.compareTo(WEAK_THRESHOLD) < 0) {
                        return QuizQuestion.DifficultyLevel.EASY;
                }

                // Average performer in this category
                return QuizQuestion.DifficultyLevel.MEDIUM;
        }
}
