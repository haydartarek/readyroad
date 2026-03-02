-- V84: Recreate lessons and lesson_pages tables for DB-driven lesson runtime.
-- JSON (lessons_content.json) is the canonical source-of-truth for content authoring;
-- data is imported into these tables via the admin import pipeline.

-- ═══════════════════════════════════════════════════════════════════════
-- lessons – one row per lesson
-- ═══════════════════════════════════════════════════════════════════════
CREATE TABLE IF NOT EXISTS lessons (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    lesson_code     VARCHAR(50)  NOT NULL UNIQUE,

    title_nl        TEXT         NOT NULL,
    title_en        TEXT         NOT NULL,
    title_fr        TEXT         NOT NULL,
    title_ar        TEXT         NOT NULL,

    description_nl  TEXT,
    description_en  TEXT,
    description_fr  TEXT,
    description_ar  TEXT,

    icon            VARCHAR(10),
    display_order   INT          NOT NULL DEFAULT 0,
    estimated_minutes INT        NOT NULL DEFAULT 5,
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,

    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_lessons_display_order (display_order),
    INDEX idx_lessons_active (is_active),
    INDEX idx_lessons_code (lesson_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ═══════════════════════════════════════════════════════════════════════
-- lesson_pages – one row per page within a lesson
-- ═══════════════════════════════════════════════════════════════════════
CREATE TABLE IF NOT EXISTS lesson_pages (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    lesson_id       BIGINT       NOT NULL,
    page_number     INT          NOT NULL,

    title_nl        TEXT         NOT NULL,
    title_en        TEXT         NOT NULL,
    title_fr        TEXT         NOT NULL,
    title_ar        TEXT         NOT NULL,

    content_nl      MEDIUMTEXT,
    content_en      MEDIUMTEXT,
    content_fr      MEDIUMTEXT,
    content_ar      MEDIUMTEXT,

    bullet_points_nl TEXT,
    bullet_points_en TEXT,
    bullet_points_fr TEXT,
    bullet_points_ar TEXT,

    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    UNIQUE KEY uk_lesson_page (lesson_id, page_number),
    INDEX idx_lesson_pages_lesson (lesson_id),

    CONSTRAINT fk_lesson_pages_lesson
        FOREIGN KEY (lesson_id) REFERENCES lessons(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
