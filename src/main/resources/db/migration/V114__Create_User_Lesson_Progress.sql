-- V114: Create user_lesson_progress table for lesson tracking
CREATE TABLE user_lesson_progress (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id         BIGINT       NOT NULL,
    lesson_id       BIGINT       NOT NULL,
    status          VARCHAR(20)  NOT NULL DEFAULT 'NOT_STARTED',
    pages_read      INT          NOT NULL DEFAULT 0,
    completed_at    TIMESTAMP    NULL,
    last_seen_at    TIMESTAMP    NULL,
    created_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE  KEY uq_user_lesson (user_id, lesson_id),
    CONSTRAINT fk_ulp_user   FOREIGN KEY (user_id)   REFERENCES users(id)   ON DELETE CASCADE,
    CONSTRAINT fk_ulp_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE
);
