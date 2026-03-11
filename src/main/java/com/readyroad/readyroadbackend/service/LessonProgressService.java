package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Lesson Progress Service
 *
 * Persists per-user lesson progress to user_lesson_progress and fires
 * LESSON_PROGRESS / NEXT_STEP notifications when a lesson is completed.
 *
 * Call markPageRead() from the lesson controller each time the user reads a
 * page. When pages_read reaches the total page count the lesson is
 * automatically marked COMPLETED and notifications are sent.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LessonProgressService {

    private final NotificationService notificationService;
    private final LessonRepository lessonRepository;
    private final UserLessonProgressRepository progressRepository;

    /**
     * Mark a page as read for the given user and lesson.
     *
     * Increments pages_read in user_lesson_progress. When pages_read reaches
     * totalPages the lesson is automatically marked COMPLETED and the
     * LESSON_PROGRESS + NEXT_STEP notifications are fired.
     *
     * @param userId     authenticated user ID
     * @param lessonId   lesson being read
     * @param totalPages total number of pages in the lesson (passed by caller)
     * @return current progress snapshot
     */
    @Transactional
    public Map<String, Object> markPageRead(Long userId, Long lessonId, int totalPages) {
        UserLessonProgress prog = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(new UserLessonProgress(userId, lessonId));

        prog.setLastSeenAt(Instant.now());

        if (!"COMPLETED".equals(prog.getStatus())) {
            int newCount = Math.min(prog.getPagesRead() + 1, totalPages);
            prog.setPagesRead(newCount);

            if (newCount >= totalPages) {
                prog.setStatus("COMPLETED");
                prog.setCompletedAt(Instant.now());
                progressRepository.save(prog);
                recordLessonCompletion(userId, lessonId);
            } else {
                prog.setStatus("IN_PROGRESS");
                progressRepository.save(prog);
            }
        } else {
            progressRepository.save(prog);
        }

        return Map.of(
                "lessonId", lessonId,
                "pagesRead", prog.getPagesRead(),
                "totalPages", totalPages,
                "status", prog.getStatus(),
                "completed", "COMPLETED".equals(prog.getStatus()));
    }

    /**
     * Get the current progress of a user for a given lesson.
     *
     * @return progress snapshot, or NOT_STARTED defaults if no row exists
     */
    public Map<String, Object> getProgress(Long userId, Long lessonId) {
        return progressRepository.findByUserIdAndLessonId(userId, lessonId)
                .map(prog -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("lessonId", prog.getLessonId());
                    m.put("pagesRead", prog.getPagesRead());
                    m.put("status", prog.getStatus());
                    m.put("completed", "COMPLETED".equals(prog.getStatus()));
                    return m;
                })
                .orElseGet(() -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("lessonId", lessonId);
                    m.put("pagesRead", 0);
                    m.put("status", "NOT_STARTED");
                    m.put("completed", false);
                    return m;
                });
    }

    /**
     * Record that a user has completed a lesson.
     *
     * Sends:
     * 1. A LESSON_PROGRESS notification for the completed lesson.
     * 2. A NEXT_STEP notification pointing to the next lesson in order (if one
     * exists).
     *
     * @param userId   ID of the user who completed the lesson
     * @param lessonId ID of the completed lesson
     */
    @Transactional
    public void recordLessonCompletion(Long userId, Long lessonId) {
        lessonRepository.findById(lessonId).ifPresentOrElse(lesson -> {
            String title = lesson.getTitleEn() != null ? lesson.getTitleEn() : "Lesson #" + lessonId;

            try {
                notificationService.createLessonProgressNotification(userId, title);
                log.info("Lesson progress notification sent: userId={}, lessonId={}", userId, lessonId);
            } catch (Exception ex) {
                log.warn("Failed to send lesson progress notification: userId={}, lessonId={}: {}",
                        userId, lessonId, ex.getMessage());
            }

            suggestNextLesson(userId, lesson.getDisplayOrder());

        }, () -> log.warn("Lesson not found for completion event: lessonId={}", lessonId));
    }

    /**
     * Suggest the next lesson based on display order.
     */
    private void suggestNextLesson(Long userId, Integer currentOrder) {
        if (currentOrder == null)
            return;

        lessonRepository.findAll().stream()
                .filter(l -> Boolean.TRUE.equals(l.getIsActive()))
                .filter(l -> l.getDisplayOrder() != null && l.getDisplayOrder() > currentOrder)
                .min((a, b) -> Integer.compare(a.getDisplayOrder(), b.getDisplayOrder()))
                .ifPresent(next -> {
                    String nextTitle = next.getTitleEn() != null ? next.getTitleEn() : "the next lesson";
                    try {
                        notificationService.createNextStepNotification(
                                userId,
                                nextTitle,
                                "/lessons/" + next.getLessonCode());
                        log.debug("Next step notification sent: userId={}, nextLessonId={}", userId, next.getId());
                    } catch (Exception ex) {
                        log.warn("Failed to send next step notification: userId={}: {}", userId, ex.getMessage());
                    }
                });
    }
}
