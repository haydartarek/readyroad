package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulation;
import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationRepository;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Admin Notification Scheduler
 *
 * Runs every day at 08:00 AM UTC and sends a daily digest to all admins:
 * - Total exams taken (all time)
 * - Exams passed vs failed
 *
 * Includes a 20-hour cooldown guard to prevent duplicate digests.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminNotificationScheduler {

    private static final int PASS_THRESHOLD = 41;
    private static final long COOLDOWN_HOURS = 20;

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final ExamSimulationRepository examSimulationRepository;
    private final UserRepository userRepository;

    @Value("${readyroad.admin-digest.enabled:true}")
    private boolean enabled;

    /**
     * Daily admin digest — runs at 08:00 AM UTC.
     */
    @Scheduled(cron = "${readyroad.admin-digest.cron-expression:0 0 8 * * *}")
    public void sendDailyAdminDigest() {
        if (!enabled) {
            log.info("Admin digest scheduler is disabled — skipping.");
            return;
        }

        log.info("Admin digest scheduler started.");

        List<User> admins = userRepository.findByRole(Role.ADMIN);
        if (admins.isEmpty()) {
            log.info("No admins found — skipping digest.");
            return;
        }

        Instant cooldownCutoff = Instant.now().minus(COOLDOWN_HOURS, ChronoUnit.HOURS);

        for (User admin : admins) {
            try {
                boolean recentDigestExists = !notificationRepository
                        .findByUserIdAndTypeAndCreatedAtAfter(
                                admin.getId(), NotificationType.ADMIN_EXAM_STATS, cooldownCutoff)
                        .isEmpty();

                if (recentDigestExists) {
                    log.debug("Skipping digest for adminId={} (already sent within {} hours)",
                            admin.getId(), COOLDOWN_HOURS);
                    continue;
                }

                long totalExams = examSimulationRepository.countByStatus(ExamSimulation.ExamStatus.COMPLETED);
                long passed = examSimulationRepository.countByStatusAndCorrectAnswersGreaterThanEqual(
                        ExamSimulation.ExamStatus.COMPLETED, PASS_THRESHOLD);
                long failed = totalExams - passed;

                notificationService.notifyAdminsDailyStats(totalExams, passed, failed);

                log.info("Daily digest sent to adminId={}: total={}, passed={}, failed={}",
                        admin.getId(), totalExams, passed, failed);
                break;

            } catch (Exception ex) {
                log.warn("Failed to process admin digest for adminId={}: {}", admin.getId(), ex.getMessage());
            }
        }

        log.info("Admin digest scheduler finished.");
    }
}
