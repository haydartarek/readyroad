package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import com.readyroad.readyroadbackend.domain.entity.SignPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.SignRandomPracticeSession;
import com.readyroad.readyroadbackend.domain.entity.UserCategoryProgress;
import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import com.readyroad.readyroadbackend.domain.entity.UserQuestionHistory;
import com.readyroad.readyroadbackend.domain.repository.CategoryRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import com.readyroad.readyroadbackend.domain.repository.SignPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.SignRandomPracticeSessionRepository;
import com.readyroad.readyroadbackend.domain.repository.UserCategoryProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.dto.StudentIntelligenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Builds the complete read model consumed by {@link StudentIntelligenceEngine}.
 */
@Service
@RequiredArgsConstructor
public class StudentIntelligenceService {

    private static final double PASS_THRESHOLD = 82.0;

    private final StudentIntelligenceEngine engine;
    private final ExamSimulationRepository examRepository;
    private final ExamSimulationAnswerRepository examAnswerRepository;
    private final SignPracticeSessionRepository signPracticeRepository;
    private final SignRandomPracticeSessionRepository randomPracticeRepository;
    private final SignExamResultRepository signExamRepository;
    private final UserCategoryProgressRepository categoryProgressRepository;
    private final CategoryRepository categoryRepository;
    private final UserQuestionHistoryRepository questionHistoryRepository;
    private final UserLessonProgressRepository lessonProgressRepository;

    @Transactional(readOnly = true)
    public StudentIntelligenceResponse getStudentIntelligence(Long userId) {
        LocalDate today = LocalDate.now(ZoneOffset.UTC);
        List<ExamSimulation> allOfficialExams = examRepository.findByUserIdOrderByStartedAtDesc(userId);
        List<SignPracticeSession> signPracticeSessions =
                signPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId);
        List<SignRandomPracticeSession> randomSessions =
                randomPracticeRepository.findAllByUserIdOrderByStartedAtDesc(userId);
        List<SignExamResult> signExamResults =
                signExamRepository.findByUserIdOrderByCompletedAtDesc(userId);
        List<UserCategoryProgress> allCategoryProgress =
                categoryProgressRepository.findByUserId(userId);
        List<UserCategoryProgress> categoryProgress = allCategoryProgress.stream()
                .filter(this::isActiveTheoreticalProgress)
                .toList();
        List<UserQuestionHistory> questionHistory =
                questionHistoryRepository.findByUserId(userId);
        List<UserLessonProgress> lessonProgress =
                lessonProgressRepository.findAllByUserId(userId);
        List<ExamSimulationAnswer> examAnswers = examAnswerRepository.findHistoryForUser(
                userId,
                ExamSimulation.ExamStatus.COMPLETED);

        List<StudentIntelligenceEngine.ScoredActivity> activities = new ArrayList<>();
        allOfficialExams.stream()
                .filter(ExamSimulation::isCompleted)
                .filter(exam -> exam.getScorePercentage() != null)
                .forEach(exam -> activities.add(new StudentIntelligenceEngine.ScoredActivity(
                        toDate(exam.getCompletedAt() != null ? exam.getCompletedAt() : exam.getStartedAt()),
                        exam.getScorePercentage(),
                        exam.isPassed(),
                        "OFFICIAL_EXAM",
                        validDuration(exam.getTimeTakenSeconds()))));

        signPracticeSessions.stream()
                .filter(session -> session.getStatus() == SignPracticeSession.SessionStatus.COMPLETED)
                .filter(session -> positive(session.getTotalQuestions()))
                .forEach(session -> {
                    double score = percentage(session.getCorrectCount(), session.getTotalQuestions());
                    activities.add(new StudentIntelligenceEngine.ScoredActivity(
                            toDate(session.getCompletedAt() != null
                                    ? session.getCompletedAt()
                                    : session.getStartedAt()),
                            score,
                            score >= PASS_THRESHOLD,
                            "SIGN_PRACTICE",
                            null));
                });

        randomSessions.stream()
                .filter(session -> session.getStatus() == SignRandomPracticeSession.SessionStatus.COMPLETED)
                .filter(session -> session.getScorePct() != null)
                .forEach(session -> activities.add(new StudentIntelligenceEngine.ScoredActivity(
                        toDate(session.getCompletedAt() != null
                                ? session.getCompletedAt()
                                : session.getStartedAt()),
                        session.getScorePct(),
                        Boolean.TRUE.equals(session.getPassed()),
                        "MIXED_SIGN_EXAM",
                        null)));

        signExamResults.stream()
                .filter(result -> result.getScorePct() != null)
                .forEach(result -> activities.add(new StudentIntelligenceEngine.ScoredActivity(
                        toDate(result.getCompletedAt()),
                        result.getScorePct(),
                        Boolean.TRUE.equals(result.getPassed()),
                        "SIGN_EXAM",
                        null)));

        Map<Long, CategoryTrendEvidence> categoryTrends = buildCategoryTrends(examAnswers, today);
        Map<Long, Category> categoriesById = categoryRepository.findAll().stream()
                .collect(Collectors.toMap(Category::getId, category -> category));
        List<StudentIntelligenceEngine.CategoryEvidence> categoryEvidence = categoryProgress.stream()
                .map(progress -> toCategoryEvidence(
                        progress,
                        categoriesById.get(progress.getCategoryId()),
                        categoryTrends.getOrDefault(progress.getCategoryId(), new CategoryTrendEvidence())))
                .toList();

        List<StudentIntelligenceEngine.QuestionEvidence> questionEvidence = questionHistory.stream()
                .map(history -> {
                    Category category = history.getQuestion() != null
                            ? history.getQuestion().getCategory()
                            : null;
                    return new StudentIntelligenceEngine.QuestionEvidence(
                            safe(history.getTimesCorrect()) + safe(history.getTimesIncorrect()),
                            safe(history.getTimesCorrect()),
                            history.getAnsweredAt() != null ? history.getAnsweredAt().toLocalDate() : null,
                            validDuration(history.getTimeTakenSeconds()),
                            category != null ? category.getId() : null,
                            category != null ? category.getCode() : null,
                            category != null ? category.getNameEn() : null,
                            category != null ? category.getNameNl() : null,
                            category != null ? category.getNameFr() : null,
                            category != null ? category.getNameAr() : null);
                })
                .toList();

        Set<LocalDate> activityDates = buildActivityDates(
                activities,
                questionHistory,
                lessonProgress);
        int lessonsStarted = (int) lessonProgress.stream()
                .filter(progress -> progress.getPagesRead() > 0
                        || !"NOT_STARTED".equals(progress.getStatus()))
                .count();
        int lessonsCompleted = (int) lessonProgress.stream()
                .filter(progress -> "COMPLETED".equals(progress.getStatus()))
                .count();
        int completedPracticeSessions = (int) signPracticeSessions.stream()
                .filter(session -> session.getStatus() == SignPracticeSession.SessionStatus.COMPLETED)
                .count()
                + (int) randomSessions.stream()
                        .filter(session -> session.getStatus() == SignRandomPracticeSession.SessionStatus.COMPLETED)
                        .count();
        int masteredSigns = (int) signExamResults.stream()
                .filter(result -> Boolean.TRUE.equals(result.getPassed()))
                .map(SignExamResult::getSignCode)
                .filter(code -> code != null && !code.isBlank())
                .distinct()
                .count();

        return engine.analyze(
                new StudentIntelligenceEngine.AnalyticsInput(
                        allOfficialExams.size(),
                        activities,
                        categoryEvidence,
                        questionEvidence,
                        activityDates,
                        lessonsStarted,
                        lessonsCompleted,
                        completedPracticeSessions,
                        masteredSigns),
                today);
    }

    private boolean isActiveTheoreticalProgress(UserCategoryProgress progress) {
        Category category = progress.getCategory();
        return category != null
                && Boolean.TRUE.equals(category.getIsActive())
                && category.getContentScope() != null
                && category.getContentScope().supportsTheoreticalExam();
    }

    private Map<Long, CategoryTrendEvidence> buildCategoryTrends(
            List<ExamSimulationAnswer> answers,
            LocalDate today) {
        LocalDate recentStart = today.minusDays(29);
        LocalDate previousStart = today.minusDays(59);
        Map<Long, CategoryTrendEvidence> result = new HashMap<>();

        for (ExamSimulationAnswer answer : answers) {
            if (answer.getAnsweredAt() == null
                    || answer.getQuestion() == null
                    || answer.getQuestion().getCategory() == null) {
                continue;
            }
            LocalDate answeredDate = toDate(answer.getAnsweredAt());
            Long categoryId = answer.getQuestion().getCategory().getId();
            CategoryTrendEvidence evidence = result.computeIfAbsent(
                    categoryId,
                    ignored -> new CategoryTrendEvidence());

            if (!answeredDate.isBefore(recentStart) && !answeredDate.isAfter(today)) {
                evidence.recentAttempts++;
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    evidence.recentCorrect++;
                }
            } else if (!answeredDate.isBefore(previousStart) && answeredDate.isBefore(recentStart)) {
                evidence.previousAttempts++;
                if (Boolean.TRUE.equals(answer.getIsCorrect())) {
                    evidence.previousCorrect++;
                }
            }
        }
        return result;
    }

    private StudentIntelligenceEngine.CategoryEvidence toCategoryEvidence(
            UserCategoryProgress progress,
            Category category,
            CategoryTrendEvidence trend) {
        return new StudentIntelligenceEngine.CategoryEvidence(
                progress.getCategoryId(),
                category != null ? category.getCode() : String.valueOf(progress.getCategoryId()),
                category != null ? category.getNameEn() : null,
                category != null ? category.getNameNl() : null,
                category != null ? category.getNameFr() : null,
                category != null ? category.getNameAr() : null,
                safe(progress.getQuestionsAttempted()),
                safe(progress.getCorrectAnswers()),
                progress.getLastPracticed() != null ? progress.getLastPracticed().toLocalDate() : null,
                trend.recentAttempts,
                trend.recentCorrect,
                trend.previousAttempts,
                trend.previousCorrect);
    }

    private Set<LocalDate> buildActivityDates(
            List<StudentIntelligenceEngine.ScoredActivity> activities,
            List<UserQuestionHistory> questions,
            List<UserLessonProgress> lessons) {
        Set<LocalDate> result = new HashSet<>();
        activities.stream()
                .map(StudentIntelligenceEngine.ScoredActivity::date)
                .filter(date -> date != null)
                .forEach(result::add);
        questions.stream()
                .map(UserQuestionHistory::getAnsweredAt)
                .filter(date -> date != null)
                .map(LocalDateTime::toLocalDate)
                .forEach(result::add);
        lessons.stream()
                .map(progress -> progress.getLastSeenAt() != null
                        ? progress.getLastSeenAt()
                        : progress.getCompletedAt())
                .filter(date -> date != null)
                .map(this::toDate)
                .forEach(result::add);
        return result;
    }

    private int safe(Integer value) {
        return value == null ? 0 : Math.max(0, value);
    }

    private boolean positive(Integer value) {
        return value != null && value > 0;
    }

    private double percentage(Integer correct, Integer total) {
        return positive(total) ? safe(correct) * 100.0 / total : 0.0;
    }

    private Integer validDuration(Integer duration) {
        return duration != null && duration > 0 ? duration : null;
    }

    private LocalDate toDate(Instant value) {
        return value == null ? null : value.atZone(ZoneOffset.UTC).toLocalDate();
    }

    private LocalDate toDate(LocalDateTime value) {
        return value == null ? null : value.toLocalDate();
    }

    private static class CategoryTrendEvidence {
        private int recentAttempts;
        private int recentCorrect;
        private int previousAttempts;
        private int previousCorrect;
    }
}
