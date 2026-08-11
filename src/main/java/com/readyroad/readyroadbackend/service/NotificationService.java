package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.NotificationDTO;
import com.readyroad.readyroadbackend.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

/**
 * Notification Service — full implementation.
 *
 * Provides:
 * - CRUD for user notifications (list, unread count, mark-read)
 * - Factory helpers for creating typed notifications (called from other
 * services)
 *
 * All read/write operations are scoped to the authenticated user (row-level
 * security).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    // ── Read operations ──────────────────────────────────────────────────────

    /**
     * Get all notifications for a user, newest first (max 50).
     *
     * @param username Spring Security principal name (username)
     * @return list of notification DTOs
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> getAllNotifications(String username) {
        Long userId = resolveUserId(username);
        log.debug("Fetching notifications for user {} (id={})", username, userId);

        return notificationRepository
                .findTop50ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(NotificationDTO::from)
                .toList();
    }

    /**
     * Get unread notification count for a user.
     * Called every 30 s by the navbar polling mechanism.
     *
     * @param username Spring Security principal name
     * @return number of unread notifications
     */
    @Transactional(readOnly = true)
    public int getUnreadCount(String username) {
        Long userId = resolveUserId(username);
        int count = notificationRepository.countUnreadByUserId(userId);
        log.debug("Unread count for user {} = {}", username, count);
        return count;
    }

    /**
     * Mark a single notification as read.
     * Validates ownership — a user cannot mark another user's notification.
     *
     * @param notificationId notification primary key
     * @param username       authenticated user's username
     */
    @Transactional
    public void markAsRead(Long notificationId, String username) {
        Long userId = resolveUserId(username);

        Notification notification = notificationRepository
                .findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> {
                    log.warn("Notification {} not found or not owned by user {}", notificationId, username);
                    return new UnauthorizedException(userId, notificationId);
                });

        if (Boolean.TRUE.equals(notification.getIsRead())) {
            log.debug("Notification {} already read", notificationId);
            return; // Idempotent
        }

        notification.markRead();
        notificationRepository.save(notification);
        log.debug("Notification {} marked as read for user {}", notificationId, username);
    }

    /**
     * Mark ALL unread notifications as read for a user.
     * Called when the user opens the notification panel.
     *
     * @param username authenticated user's username
     * @return number of notifications marked as read
     */
    @Transactional
    public int markAllAsRead(String username) {
        Long userId = resolveUserId(username);
        int updated = notificationRepository.markAllReadByUserId(userId, Instant.now());
        log.info("Marked {} notifications as read for user {}", updated, username);
        return updated;
    }

    // ── Factory helpers (called by other services) ────────────────────────────

    /**
     * Create an EXAM_PASSED notification.
     *
     * @param userId recipient user ID
     * @param examId the exam that was completed
     * @param score  correct answers count (e.g. 43)
     * @param total  total questions (50)
     */
    @Transactional
    public void createExamPassedNotification(Long userId, Long examId, int score, int total) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_PASSED)
                .title("🎉 Exam Passed!")
                .message(String.format(
                        "Congratulations! You passed the exam with %d/%d (%d%%). Great job!",
                        score, total, pct))
                .messageKey("notif.msg.exam_passed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d}", score, total, pct))
                .link("/exam/results/" + examId)
                .build());
    }

    /**
     * Create an EXAM_FAILED notification.
     *
     * @param userId      recipient user ID
     * @param examId      the exam that was completed
     * @param score       correct answers count
     * @param total       total questions (50)
     * @param pointsShort how many more correct answers were needed
     */
    @Transactional
    public void createExamFailedNotification(Long userId, Long examId, int score, int total, int pointsShort) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_FAILED)
                .title("📚 Exam Result: Not Passed")
                .message(String.format(
                        "You scored %d/%d (%d%%). You needed %d more correct answers to pass. Keep practicing!",
                        score, total, pct, pointsShort))
                .messageKey("notif.msg.exam_failed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d,\"needed\":%d}", score, total, pct,
                        pointsShort))
                .link("/exam/results/" + examId)
                .build());
    }

    /**
     * Create an EXAM_RESULT notification when a user abandons (cancels) an exam.
     * Uses the neutral EXAM_RESULT type — not EXAM_FAILED, since abandonment is
     * a deliberate user action rather than a performance failure.
     *
     * @param userId recipient user ID
     * @param examId the exam that was abandoned
     */
    @Transactional
    public void createExamAbandonedNotification(Long userId, Long examId) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_RESULT)
                .title("Exam Cancelled")
                .message("Your exam session was cancelled. You can start a new exam whenever you're ready.")
                .messageKey("notif.msg.exam_abandoned")
                .messageParams("{}")
                .link("/exam")
                .build());
    }

    /**
     * Create an EXAM_PASSED notification for the mixed traffic-sign exam shown in
     * /practice/random.
     */
    @Transactional
    public void createRandomSignExamPassedNotification(Long userId, Long sessionId, int score, int total) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_PASSED)
                .title("🎉 Mixed Sign Exam Passed!")
                .message(String.format(
                        "You passed the mixed traffic-sign exam with %d/%d (%d%%).",
                        score, total, pct))
                .messageKey("notif.msg.sign_random_exam_passed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d}", score, total, pct))
                .link("/dashboard?section=exam-results&randomSignExamId=" + sessionId)
                .build());
    }

    /**
     * Create an EXAM_FAILED notification for the mixed traffic-sign exam shown in
     * /practice/random.
     */
    @Transactional
    public void createRandomSignExamFailedNotification(
            Long userId,
            Long sessionId,
            int score,
            int total,
            int pointsShort) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_FAILED)
                .title("📚 Mixed Sign Exam Result")
                .message(String.format(
                        "You scored %d/%d (%d%%). You needed %d more correct answers to pass the mixed traffic-sign exam.",
                        score, total, pct, pointsShort))
                .messageKey("notif.msg.sign_random_exam_failed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d,\"needed\":%d}", score, total, pct,
                        pointsShort))
                .link("/dashboard?section=exam-results&randomSignExamId=" + sessionId)
                .build());
    }

    /**
     * Create an EXAM_PASSED notification for one sign-specific exam result.
     */
    @Transactional
    public void createSignExamPassedNotification(Long userId, Long resultId, int score, int total) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_PASSED)
                .title("🎉 Sign Exam Passed!")
                .message(String.format(
                        "You passed a traffic-sign exam with %d/%d (%d%%).",
                        score, total, pct))
                .messageKey("notif.msg.sign_exam_passed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d}", score, total, pct))
                .link("/dashboard?section=exam-results&signExamResultId=" + resultId)
                .build());
    }

    /**
     * Create an EXAM_FAILED notification for one sign-specific exam result.
     */
    @Transactional
    public void createSignExamFailedNotification(
            Long userId,
            Long resultId,
            int score,
            int total,
            int pointsShort) {
        int pct = (int) Math.round((score * 100.0) / total);
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.EXAM_FAILED)
                .title("📚 Sign Exam Result")
                .message(String.format(
                        "You scored %d/%d (%d%%). You needed %d more correct answers to pass this traffic-sign exam.",
                        score, total, pct, pointsShort))
                .messageKey("notif.msg.sign_exam_failed")
                .messageParams(String.format("{\"score\":%d,\"total\":%d,\"pct\":%d,\"needed\":%d}", score, total, pct,
                        pointsShort))
                .link("/dashboard?section=exam-results&signExamResultId=" + resultId)
                .build());
    }

    /**
     * Create a WEAK_AREA notification when a category stays below 60% accuracy.
     *
     * @param userId       recipient user ID
     * @param categoryNameEn English category name used by the legacy fallback
     * @param categoryNameAr Arabic category name
     * @param categoryNameNl Dutch category name
     * @param categoryNameFr French category name
     */
    @Transactional
    public void createWeakAreaNotification(
            Long userId,
            String categoryNameEn,
            String categoryNameAr,
            String categoryNameNl,
            String categoryNameFr) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.WEAK_AREA)
                .title("⚠️ Weak Area Detected")
                .message(String.format(
                        "Your accuracy in '%s' is below 60%%. Consider reviewing this topic before your next exam.",
                        categoryNameEn))
                .messageKey("notif.msg.weak_area")
                .messageParams(String.format(
                        "{\"category\":\"%s\",\"categoryEn\":\"%s\",\"categoryAr\":\"%s\",\"categoryNl\":\"%s\",\"categoryFr\":\"%s\"}",
                        escapeJson(categoryNameEn),
                        escapeJson(categoryNameEn),
                        escapeJson(categoryNameAr),
                        escapeJson(categoryNameNl),
                        escapeJson(categoryNameFr)))
                .link("/dashboard?section=weak-areas")
                .build());
    }

    private static String escapeJson(String value) {
        return value == null ? "" : value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Create an ACHIEVEMENT notification for a study-streak milestone.
     *
     * @param userId     recipient user ID
     * @param streakDays the streak milestone reached (e.g. 7, 14, 30)
     */
    @Transactional
    public void createStreakNotification(Long userId, int streakDays) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.STREAK_ACHIEVED)
                .title("🔥 " + streakDays + "-Day Study Streak!")
                .message(String.format(
                        "Amazing! You've studied for %d days in a row. Keep it up to master your driving theory!",
                        streakDays))
                .messageKey("notif.msg.streak_achieved")
                .messageParams(String.format("{\"days\":%d}", streakDays))
                .link("/dashboard")
                .build());
    }

    /**
     * Create an ACHIEVEMENT notification when a user earns a new badge/milestone.
     *
     * @param userId      recipient user ID
     * @param title       achievement title (e.g. "🏆 First Exam Completed!")
     * @param description detailed description of the achievement
     */
    @Transactional
    public void createAchievementNotification(Long userId, String title, String description, String messageKey) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.ACHIEVEMENT)
                .title(title)
                .message(description)
                .messageKey(messageKey)
                .link("/dashboard")
                .build());
    }

    /**
     * Create a STUDY_REMINDER notification to nudge an inactive user.
     *
     * @param userId  recipient user ID
     * @param title   notification headline
     * @param message notification body
     */
    @Transactional
    public void createStudyReminderNotification(Long userId, String title, String message, int inactiveDays) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.STUDY_REMINDER)
                .title(title)
                .message(message)
                .messageKey("notif.msg.study_reminder")
                .messageParams(String.format("{\"days\":%d}", inactiveDays))
                .link("/practice")
                .build());
    }

    /**
     * Create a generic SYSTEM notification (admin broadcasts, feature
     * announcements, …).
     *
     * @param userId  recipient user ID (null = not used here; caller handles
     *                fan-out)
     * @param title   notification headline
     * @param message full notification text
     * @param link    optional deep-link
     */
    @Transactional
    public void createSystemNotification(Long userId, String title, String message, String link) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.SYSTEM)
                .title(title)
                .message(message)
                .link(link)
                .build());
    }

    // ── Admin fan-out helpers ─────────────────────────────────────────────────

    /**
     * Notify every user with ROLE_ADMIN.
     * Silently skips if no admins are found.
     */
    @Transactional
    public void notifyAllAdmins(String title, String message, String link) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            try {
                save(Notification.builder()
                        .userId(admin.getId())
                        .type(NotificationType.SYSTEM)
                        .title(title)
                        .message(message)
                        .link(link)
                        .build());
            } catch (Exception ex) {
                log.warn("Failed to notify admin userId={}: {}", admin.getId(), ex.getMessage());
            }
        }
        log.info("Admin fan-out sent to {} admins: title={}", admins.size(), title);
    }

    /**
     * Notify all admins about a new user registration.
     */
    @Transactional
    public void notifyAdminsNewUser(String newUsername, String newEmail) {
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            try {
                save(Notification.builder()
                        .userId(admin.getId())
                        .type(NotificationType.ADMIN_NEW_USER)
                        .title("New user registered")
                        .message(String.format("User '%s' (%s) just created an account.", newUsername, newEmail))
                        .messageKey("notif.msg.admin.new_user")
                        .messageParams(String.format("{\"username\":\"%s\",\"email\":\"%s\"}", newUsername, newEmail))
                        .link("/admin/users")
                        .build());
            } catch (Exception ex) {
                log.warn("Failed to notify admin userId={} about new user: {}", admin.getId(), ex.getMessage());
            }
        }
    }

    /**
     * Notify all admins with a daily exam stats digest.
     */
    @Transactional
    public void notifyAdminsDailyStats(long totalExams, long passed, long failed) {
        String message = String.format(
                "Today: %d exams taken — %d passed (%.0f%%), %d failed.",
                totalExams, passed,
                totalExams > 0 ? (passed * 100.0 / totalExams) : 0,
                failed);
        List<User> admins = userRepository.findByRole(Role.ADMIN);
        for (User admin : admins) {
            try {
                save(Notification.builder()
                        .userId(admin.getId())
                        .type(NotificationType.ADMIN_EXAM_STATS)
                        .title("Daily exam summary")
                        .message(message)
                        .messageKey("notif.msg.admin.exam_stats")
                        .messageParams(String.format(
                                "{\"total\":%d,\"passed\":%d,\"failed\":%d}", totalExams, passed, failed))
                        .link("/admin/dashboard")
                        .build());
            } catch (Exception ex) {
                log.warn("Failed to send daily stats to admin userId={}: {}", admin.getId(), ex.getMessage());
            }
        }
    }

    /**
     * Send a manual platform-wide alert from an admin to all users.
     */
    @Transactional
    public void broadcastPlatformAlert(String title, String message, String link) {
        List<User> allUsers = userRepository.findByIsActiveTrue();
        for (User user : allUsers) {
            try {
                save(Notification.builder()
                        .userId(user.getId())
                        .type(NotificationType.ADMIN_PLATFORM_ALERT)
                        .title(title)
                        .message(message)
                        .link(link)
                        .build());
            } catch (Exception ex) {
                log.warn("Failed to broadcast to userId={}: {}", user.getId(), ex.getMessage());
            }
        }
        log.info("Platform alert broadcast to {} users", allUsers.size());
    }

    /**
     * Notify a user that they completed a lesson.
     */
    @Transactional
    public void createLessonProgressNotification(Long userId, String lessonTitle) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.LESSON_PROGRESS)
                .title("Lesson completed!")
                .message(String.format("You finished the lesson: \"%s\". Keep it up!", lessonTitle))
                .messageKey("notif.msg.lesson_progress")
                .messageParams(String.format("{\"lesson\":\"%s\"}", lessonTitle))
                .link("/lessons")
                .build());
    }

    /**
     * Suggest the next learning step for a user.
     */
    @Transactional
    public void createNextStepNotification(Long userId, String lessonTitle, String link) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.NEXT_STEP)
                .title("What to study next")
                .message("Continue to: " + lessonTitle)
                .messageKey("notif.msg.next_step")
                .messageParams(String.format("{\"lesson\":\"%s\"}",
                        lessonTitle.replace("\\", "\\\\").replace("\"", "\\\"")))
                .link(link)
                .build());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Long resolveUserId(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username))
                .getId();
    }

    private void save(Notification notification) {
        if (notification.getCreatedAt() == null) {
            notification.setCreatedAt(Instant.now());
        }
        notificationRepository.save(notification);
        log.info("Created notification: type={}, userId={}", notification.getType(), notification.getUserId());
    }
}
