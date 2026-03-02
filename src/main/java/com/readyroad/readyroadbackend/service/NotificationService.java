package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
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
 *  - CRUD for user notifications (list, unread count, mark-read)
 *  - Factory helpers for creating typed notifications (called from other services)
 *
 * All read/write operations are scoped to the authenticated user (row-level security).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository         userRepository;

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
            return;  // Idempotent
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
        int updated = notificationRepository.markAllReadByUserId(userId);
        log.info("Marked {} notifications as read for user {}", updated, username);
        return updated;
    }

    // ── Factory helpers (called by other services) ────────────────────────────

    /**
     * Create an EXAM_PASSED notification.
     *
     * @param userId  recipient user ID
     * @param examId  the exam that was completed
     * @param score   correct answers count (e.g. 43)
     * @param total   total questions (50)
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
                .link("/exam/results/" + examId)
                .build());
    }

    /**
     * Create a WEAK_AREA notification when a category stays below 60% accuracy.
     *
     * @param userId       recipient user ID
     * @param categoryName category that is weak
     */
    @Transactional
    public void createWeakAreaNotification(Long userId, String categoryName) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.WEAK_AREA)
                .title("⚠️ Weak Area Detected")
                .message(String.format(
                        "Your accuracy in '%s' is below 60%%. Consider reviewing this topic before your next exam.",
                        categoryName))
                .link("/analytics/weak-areas")
                .build());
    }

    /**
     * Create an ACHIEVEMENT notification for a study-streak milestone.
     *
     * @param userId      recipient user ID
     * @param streakDays  the streak milestone reached (e.g. 7, 14, 30)
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
    public void createAchievementNotification(Long userId, String title, String description) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.ACHIEVEMENT)
                .title(title)
                .message(description)
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
    public void createStudyReminderNotification(Long userId, String title, String message) {
        save(Notification.builder()
                .userId(userId)
                .type(NotificationType.STUDY_REMINDER)
                .title(title)
                .message(message)
                .link("/quiz")
                .build());
    }

    /**
     * Create a generic SYSTEM notification (admin broadcasts, feature announcements, …).
     *
     * @param userId  recipient user ID (null = not used here; caller handles fan-out)
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
