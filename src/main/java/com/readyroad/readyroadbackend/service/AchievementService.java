package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Achievement;
import com.readyroad.readyroadbackend.domain.entity.AchievementType;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.repository.AchievementRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Achievement Service — awards one-time badges to users.
 *
 * Called from ExamService.completeExam() after every exam completion.
 * Each achievement is awarded at most once per user (DB UNIQUE + existsByUserIdAndType check).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {

    private final AchievementRepository achievementRepository;
    private final ExamSimulationRepository examSimulationRepository;
    private final NotificationService notificationService;

    /**
     * Check and award all applicable exam-based achievements for a user.
     * Called after every exam completion (pass or fail).
     *
     * @param userId  the user who completed the exam
     * @param examId  the completed exam ID (for metadata)
     * @param passed  whether the exam was passed
     * @param score   correct answers count (out of 50)
     */
    @Transactional
    public void checkAndAwardExamAchievements(Long userId, Long examId, boolean passed, int score) {
        log.debug("Checking exam achievements for userId={}, examId={}, passed={}, score={}",
                userId, examId, passed, score);

        // Count total completed exams for this user
        long totalCompleted = examSimulationRepository.countByUserIdAndStatus(
                userId, ExamSimulation.ExamStatus.COMPLETED);

        // Count total passed exams
        // We use all COMPLETED exams and filter by score >= 41 via Java (simpler and avoids new query)
        long totalPassed = examSimulationRepository
                .findByUserIdAndStatusOrderByCompletedAtDesc(userId, ExamSimulation.ExamStatus.COMPLETED)
                .stream()
                .filter(e -> e.getCorrectAnswers() != null && e.getCorrectAnswers() >= 41)
                .count();

        // ── FIRST_EXAM: first ever exam completed (pass or fail) ──────────────
        if (totalCompleted == 1) {
            award(userId, AchievementType.FIRST_EXAM,
                    "🎓 First Exam Completed!",
                    "You completed your very first practice exam. The journey to your driving license has begun!",
                    examId);
        }

        // ── PERFECT_SCORE: 50/50 ─────────────────────────────────────────────
        if (score == 50 && passed) {
            award(userId, AchievementType.PERFECT_SCORE,
                    "⭐ Perfect Score!",
                    "Incredible! You answered all 50 questions correctly. You're exam-ready!",
                    examId);
        }

        // ── FIVE_EXAMS_PASSED ────────────────────────────────────────────────
        if (totalPassed == 5) {
            award(userId, AchievementType.FIVE_EXAMS_PASSED,
                    "🏅 5 Exams Passed!",
                    "You have now passed 5 practice exams. Excellent consistency!",
                    examId);
        }

        // ── TEN_EXAMS_PASSED ─────────────────────────────────────────────────
        if (totalPassed == 10) {
            award(userId, AchievementType.TEN_EXAMS_PASSED,
                    "🏆 10 Exams Passed!",
                    "10 exams passed! You are truly mastering the Belgian driving theory.",
                    examId);
        }

        // ── PASSING_STREAK_3: 3 consecutive passed exams ─────────────────────
        if (passed) {
            checkPassingStreak3(userId, examId);
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Award an achievement if the user doesn't already have it.
     * The DB UNIQUE constraint is a safety net; existsBy check avoids constraint violation logging.
     */
    private void award(Long userId, AchievementType type, String title, String description, Long examId) {
        if (achievementRepository.existsByUserIdAndType(userId, type)) {
            log.debug("Achievement {} already awarded to userId={}", type, userId);
            return;
        }

        Achievement achievement = Achievement.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .description(description)
                .achievedAt(Instant.now())
                .metadata(String.format("{\"examId\":%d}", examId))
                .build();

        try {
            achievementRepository.save(achievement);
            notificationService.createAchievementNotification(userId, title, description);
            log.info("🏆 Achievement awarded: type={}, userId={}", type, userId);
        } catch (Exception ex) {
            // DataIntegrityViolationException if race condition — safe to ignore
            log.warn("Failed to save achievement type={} for userId={}: {}", type, userId, ex.getMessage());
        }
    }

    /**
     * Check if the user has passed the last 3 exams in a row (PASSING_STREAK_3).
     * Looks at the 3 most recent COMPLETED exams for the user.
     */
    private void checkPassingStreak3(Long userId, Long examId) {
        if (achievementRepository.existsByUserIdAndType(userId, AchievementType.PASSING_STREAK_3)) {
            return; // Already awarded
        }

        var recentExams = examSimulationRepository
                .findByUserIdAndStatusOrderByCompletedAtDesc(userId, ExamSimulation.ExamStatus.COMPLETED);

        if (recentExams.size() < 3) return;

        // Check if first 3 (most recent) are all passed (score >= 41)
        boolean streak = recentExams.subList(0, 3).stream()
                .allMatch(e -> e.getCorrectAnswers() != null && e.getCorrectAnswers() >= 41);

        if (streak) {
            award(userId, AchievementType.PASSING_STREAK_3,
                    "🔥 3-Exam Winning Streak!",
                    "You passed 3 exams in a row! Your driving theory knowledge is rock solid.",
                    examId);
        }
    }
}
