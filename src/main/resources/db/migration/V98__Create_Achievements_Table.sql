-- V98: Create achievements table for user milestone tracking
-- Each user can earn each achievement type exactly once (UNIQUE constraint prevents duplicates).

CREATE TABLE IF NOT EXISTS achievements (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    type           VARCHAR(50)  NOT NULL COMMENT 'AchievementType enum value',
    title          VARCHAR(255) NOT NULL,
    description    TEXT,
    achieved_at    DATETIME(6)  NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    metadata       JSON         NULL     COMMENT 'Extra info (examId, score, etc.)',

    PRIMARY KEY (id),
    UNIQUE KEY uq_achievement_user_type (user_id, type),
    INDEX idx_achievement_user_id (user_id),
    INDEX idx_achievement_achieved_at (achieved_at),

    CONSTRAINT fk_achievement_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='Tracks one-time achievement badges earned by users';
