-- ============================================================
-- V97: Create Notifications Table
-- ============================================================
-- Stores all user notifications for:
--   EXAM_RESULT     → after completing an exam simulation
--   EXAM_PASSED     → when user passes an exam (≥ 41/50)
--   EXAM_FAILED     → when user fails an exam  (< 41/50)
--   STREAK_ACHIEVED → when user hits a study streak milestone
--   WEAK_AREA       → when user has a persistent weak area
--   ACHIEVEMENT     → general achievements/milestones
--   STUDY_REMINDER  → nudge to keep studying
--   SYSTEM          → system-wide announcements
-- ============================================================

CREATE TABLE IF NOT EXISTS notifications (
    id          BIGINT         AUTO_INCREMENT PRIMARY KEY,
    user_id     BIGINT         NOT NULL,
    type        VARCHAR(50)    NOT NULL,
    title       VARCHAR(255)   NOT NULL,
    message     TEXT           NOT NULL,
    link        VARCHAR(500)   NULL COMMENT 'Optional deep-link for the notification',
    is_read     BOOLEAN        NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    read_at     TIMESTAMP      NULL,

    CONSTRAINT fk_notification_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

    INDEX idx_notifications_user_id      (user_id),
    INDEX idx_notifications_user_unread  (user_id, is_read),
    INDEX idx_notifications_created_at   (created_at)
) ENGINE=InnoDB
  DEFAULT CHARSET=utf8mb4
  COLLATE=utf8mb4_unicode_ci
  COMMENT='User notifications table';
