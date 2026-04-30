package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Study Reminder Scheduler — daily cron job that nudges inactive users.
 *
 * Logic:
 * 1. Runs daily at 10:00 AM UTC (configurable via cron-expression).
 * 2. For each active user, checks their most recent answered_at date.
 * 3. If the user has been inactive for ≥ {@code daysInactive} days:
 * - AND no STUDY_REMINDER was sent in the last {@code cooldownHours} hours:
 * → Sends a STUDY_REMINDER notification.
 *
 * Configuration properties (application.yml):
 * readyroad.study-reminder.enabled (default: true)
 * readyroad.study-reminder.days-inactive (default: 3)
 * readyroad.study-reminder.cooldown-hours (default: 24)
 * readyroad.study-reminder.cron-expression (default: "0 0 10 * * *")
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudyReminderScheduler {

    private final UserRepository userRepository;
    private final UserQuestionHistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Value("${readyroad.study-reminder.enabled:true}")
    private boolean enabled;

    @Value("${readyroad.study-reminder.days-inactive:3}")
    private int daysInactive;

    @Value("${readyroad.study-reminder.cooldown-hours:24}")
    private long cooldownHours;

    /**
     * Daily study reminder job — runs at 10:00 AM UTC every day.
     * Cron: second minute hour day month weekday
     */
    @Scheduled(cron = "${readyroad.study-reminder.cron-expression:0 0 10 * * *}")
    public void sendStudyReminders() {
        if (!enabled) {
            log.info("Study reminder scheduler is disabled — skipping.");
            return;
        }

        log.info("⏰ Study reminder scheduler started (daysInactive={}, cooldownHours={})",
                daysInactive, cooldownHours);

        List<User> activeUsers = userRepository.findByIsActiveTrue();
        LocalDate inactivityThreshold = LocalDate.now().minusDays(daysInactive);
        Instant cooldownCutoff = Instant.now().minus(cooldownHours, ChronoUnit.HOURS);

        int sent = 0;
        int skipped = 0;

        for (User user : activeUsers) {
            try {
                if (shouldSendReminder(user.getId(), inactivityThreshold, cooldownCutoff)) {
                    notificationService.createStudyReminderNotification(
                            user.getId(),
                            "📚 Time to Study!",
                            String.format(
                                    "You haven't practiced in %d days. A short session today keeps your knowledge fresh. Let's go!",
                                    daysInactive),
                            daysInactive);
                    sent++;
                    log.debug("Study reminder sent to userId={}", user.getId());
                } else {
                    skipped++;
                }
            } catch (Exception ex) {
                log.warn("Failed to process study reminder for userId={}: {}", user.getId(), ex.getMessage());
            }
        }

        log.info("✅ Study reminder scheduler finished: sent={}, skipped={}, total={}",
                sent, skipped, activeUsers.size());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Determine whether a user should receive a study reminder.
     *
     * Returns true if:
     * (a) The user has never studied OR last studied before the inactivity
     * threshold.
     * (b) No STUDY_REMINDER was sent within the cooldown window.
     */
    private boolean shouldSendReminder(Long userId, LocalDate inactivityThreshold, Instant cooldownCutoff) {
        // Check last activity
        LocalDate lastDate = historyRepository.findMostRecentAnsweredDateByUserId(userId);

        if (lastDate != null) {
            if (!lastDate.isBefore(inactivityThreshold)) {
                // User was active recently — no reminder needed
                return false;
            }
        }
        // If lastDate is null: user has NEVER practiced → always eligible for
        // reminder

        // Check cooldown — don't spam
        boolean recentlySent = !notificationRepository
                .findByUserIdAndTypeAndCreatedAtAfter(userId, NotificationType.STUDY_REMINDER, cooldownCutoff)
                .isEmpty();

        return !recentlySent;
    }
}
