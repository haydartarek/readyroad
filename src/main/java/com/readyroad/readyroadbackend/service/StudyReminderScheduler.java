package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** Checks persisted learning activity every minute; reminders have a 24-hour cooldown. */
@Component
@RequiredArgsConstructor
@Slf4j
public class StudyReminderScheduler {

    private final UserRepository userRepository;
    private final AdminLearningStore learningStore;
    private final NotificationRepository notificationRepository;
    private final NotificationService notificationService;

    @Value("${readyroad.study-reminder.enabled:true}")
    private boolean enabled;

    @Value("${readyroad.study-reminder.hours-inactive:24}")
    private int hoursInactive;

    @Value("${readyroad.study-reminder.cooldown-hours:24}")
    private long cooldownHours;

    @Scheduled(cron = "${readyroad.study-reminder.cron-expression:0 * * * * *}", zone = "UTC")
    public void sendStudyReminders() {
        if (!enabled) {
            log.info("Study reminder scheduler is disabled — skipping.");
            return;
        }

        log.debug("Study reminder scheduler started (hoursInactive={}, cooldownHours={})",
                hoursInactive, cooldownHours);

        List<User> activeUsers = userRepository.findByRole(com.readyroad.readyroadbackend.domain.enums.Role.USER)
                .stream()
                .filter(user -> Boolean.TRUE.equals(user.getIsActive()))
                .toList();
        LocalDateTime inactivityThreshold = LocalDateTime.now(java.time.ZoneOffset.UTC).minusHours(hoursInactive);
        Instant cooldownCutoff = Instant.now().minus(cooldownHours, ChronoUnit.HOURS);

        int sent = 0;
        int skipped = 0;

        for (User user : activeUsers) {
            try {
                var profile = learningStore.findStudent(user.getId());
                LocalDateTime lastActivity = profile != null && profile.lastActiveAt() != null
                        ? profile.lastActiveAt()
                        : user.getCreatedAt();
                if (shouldSendReminder(user.getId(), lastActivity, inactivityThreshold, cooldownCutoff)) {
                    notificationService.createStudyReminderNotification(
                            user.getId(),
                            "📚 Time to Study!",
                            String.format(
                                    "You haven't practiced in %d days. A short session today keeps your knowledge fresh. Let's go!",
                                    Math.max(1, hoursInactive / 24)),
                            Math.max(1, hoursInactive / 24));
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
     * (a) The user's latest known activity is at or before the inactivity threshold.
     * (b) No STUDY_REMINDER was sent within the cooldown window.
     */
    boolean shouldSendReminder(
            Long userId,
            LocalDateTime lastActivity,
            LocalDateTime inactivityThreshold,
            Instant cooldownCutoff) {
        if (lastActivity == null || lastActivity.isAfter(inactivityThreshold)) {
            return false;
        }

        // Check cooldown — don't spam
        boolean recentlySent = !notificationRepository
                .findByUserIdAndTypeAndCreatedAtAfter(userId, NotificationType.STUDY_REMINDER, cooldownCutoff)
                .isEmpty();

        return !recentlySent;
    }
}
