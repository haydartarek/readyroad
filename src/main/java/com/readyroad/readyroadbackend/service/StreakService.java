package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.NotificationType;
import com.readyroad.readyroadbackend.domain.repository.NotificationRepository;
import com.readyroad.readyroadbackend.domain.repository.UserQuestionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Streak Service — calculates consecutive study days and fires milestone notifications.
 *
 * Streak milestones: 3, 7, 14, 30 days.
 * Dedup: if a STREAK_ACHIEVED notification was sent in the last 12 hours, skip.
 * Called from PracticeService after every practice answer submission.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StreakService {

    private final UserQuestionHistoryRepository historyRepository;
    private final NotificationRepository        notificationRepository;
    private final NotificationService           notificationService;

    /** Streak milestone days that trigger a notification */
    private static final Set<Integer> MILESTONES = Set.of(3, 7, 14, 30);

    /** Minimum gap between two STREAK_ACHIEVED notifications (12 h) */
    private static final long DEDUP_HOURS = 12;

    /**
     * Calculate the user's current study streak and, if a milestone is reached,
     * fire a STREAK_ACHIEVED notification (deduped within 12 h).
     *
     * This method is called after every practice answer and must never throw
     * an exception that would roll back the answer submission.
     *
     * @param userId the authenticated user's database ID
     */
    @Transactional
    public void updateStreakAndNotify(Long userId) {
        try {
            int streak = calculateStreak(userId);
            log.debug("Current streak for userId={}: {} days", userId, streak);

            if (streak == 0 || !MILESTONES.contains(streak)) {
                return; // No milestone reached
            }

            // Dedup: don't send if we already sent a STREAK_ACHIEVED within the last 12 h
            Instant cutoff = Instant.now().minusSeconds(DEDUP_HOURS * 3600);
            boolean recentlySent = !notificationRepository
                    .findByUserIdAndTypeAndCreatedAtAfter(userId, NotificationType.STREAK_ACHIEVED, cutoff)
                    .isEmpty();

            if (recentlySent) {
                log.debug("STREAK_ACHIEVED notification skipped (sent within {}h) for userId={}", DEDUP_HOURS, userId);
                return;
            }

            notificationService.createStreakNotification(userId, streak);
            log.info("🔥 STREAK_ACHIEVED notification sent: userId={}, streak={} days", userId, streak);

        } catch (Exception ex) {
            // Must never break the answer submission flow
            log.warn("StreakService.updateStreakAndNotify failed for userId={}: {}", userId, ex.getMessage());
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Calculate consecutive study days using answered_at history.
     *
     * Algorithm:
     *  1. Load distinct answered dates (descending order) from the repository.
     *  2. If most recent date is neither today nor yesterday → streak is 0.
     *  3. Walk backwards counting consecutive days that differ by exactly 1.
     */
    private int calculateStreak(Long userId) {
        List<String> rawDates = historyRepository.findDistinctAnswerDatesByUserId(userId);

        if (rawDates == null || rawDates.isEmpty()) {
            return 0;
        }

        List<LocalDate> dates = rawDates.stream()
                .filter(s -> s != null && !s.isEmpty())
                .map(LocalDate::parse)
                .sorted(Comparator.reverseOrder())
                .collect(Collectors.toList());

        if (dates.isEmpty()) return 0;

        LocalDate today     = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate mostRecent = dates.get(0);

        // Streak is broken if last practice was before yesterday
        if (mostRecent.isBefore(yesterday)) {
            return 0;
        }

        int streak = 1;
        for (int i = 1; i < dates.size(); i++) {
            LocalDate prev    = dates.get(i - 1);
            LocalDate current = dates.get(i);
            if (prev.minusDays(1).equals(current)) {
                streak++;
            } else {
                break; // Gap found — streak ends
            }
        }
        return streak;
    }
}
