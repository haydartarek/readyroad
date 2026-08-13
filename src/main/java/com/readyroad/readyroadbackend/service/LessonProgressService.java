package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.UserLessonProgress;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.UserLessonProgressRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
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
    private final UserRepository userRepository;

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
     * @param pageNumber optional current page number (1-based) when the frontend
     *                   knows which page the learner actually reached
     * @return current progress snapshot
     */
    @Transactional
    public Map<String, Object> markPageRead(Long userId, Long lessonId, int totalPages, Integer pageNumber) {
        UserLessonProgress prog = progressRepository
                .findByUserIdAndLessonId(userId, lessonId)
                .orElse(new UserLessonProgress(userId, lessonId));

        int normalizedTotalPages = Math.max(totalPages, 1);
        int targetPage = pageNumber != null && pageNumber > 0
                ? Math.min(pageNumber, normalizedTotalPages)
                : Math.min(Math.max(prog.getPagesRead() + 1, 1), normalizedTotalPages);

        prog.setLastSeenAt(Instant.now());
        prog.setLanguageCode(userRepository.findById(userId)
                .map(user -> user.getPreferredLanguage())
                .filter(language -> java.util.Set.of("en", "nl", "fr", "ar").contains(language))
                .orElse(null));

        if (!"COMPLETED".equals(prog.getStatus())) {
            int newCount = Math.max(prog.getPagesRead(), targetPage);
            prog.setPagesRead(newCount);

            if (newCount >= normalizedTotalPages) {
                prog.setStatus("COMPLETED");
                if (prog.getCompletedAt() == null) {
                    prog.setCompletedAt(Instant.now());
                }
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
                "totalPages", normalizedTotalPages,
                "currentPage", targetPage,
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
                    m.put("completedAt", prog.getCompletedAt());
                    m.put("lastSeenAt", prog.getLastSeenAt());
                    return m;
                })
                .orElseGet(() -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("lessonId", lessonId);
                    m.put("pagesRead", 0);
                    m.put("status", "NOT_STARTED");
                    m.put("completed", false);
                    m.put("completedAt", null);
                    m.put("lastSeenAt", null);
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
