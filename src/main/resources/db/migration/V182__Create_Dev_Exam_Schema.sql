-- ==========================================================================
-- V182 — Developer Skills Assessment System — Schema
-- 19 software-engineering exam categories, multilingual (en/ar/nl/fr).
-- ==========================================================================

-- 1. Category master ---------------------------------------------------
CREATE TABLE dev_exam_categories (
    id          INT          NOT NULL AUTO_INCREMENT,
    code        VARCHAR(50)  NOT NULL,
    icon        VARCHAR(100)         NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_dev_cat_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. Category translations (one row per category × language) -----------
CREATE TABLE dev_exam_category_i18n (
    id            INT          NOT NULL AUTO_INCREMENT,
    category_id   INT          NOT NULL,
    language_code CHAR(2)      NOT NULL,
    name          VARCHAR(255) NOT NULL,
    description   TEXT                 NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_dev_cat_lang (category_id, language_code),
    CONSTRAINT fk_dci_cat FOREIGN KEY (category_id)
        REFERENCES dev_exam_categories (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 3. Questions ---------------------------------------------------------
CREATE TABLE dev_exam_questions (
    id             BIGINT  NOT NULL AUTO_INCREMENT,
    category_id    INT     NOT NULL,
    difficulty     ENUM('BEGINNER','INTERMEDIATE','ADVANCED') NOT NULL,
    question_en    TEXT    NOT NULL,
    question_ar    TEXT            NULL,
    question_nl    TEXT            NULL,
    question_fr    TEXT            NULL,
    explanation_en TEXT            NULL,
    explanation_ar TEXT            NULL,
    explanation_nl TEXT            NULL,
    explanation_fr TEXT            NULL,
    is_active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_deq_cat_diff (category_id, difficulty),
    CONSTRAINT fk_deq_cat FOREIGN KEY (category_id)
        REFERENCES dev_exam_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 4. Answer choices (4 per question) -----------------------------------
CREATE TABLE dev_exam_choices (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    question_id BIGINT       NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    is_correct  BOOLEAN      NOT NULL DEFAULT FALSE,
    text_en     VARCHAR(600) NOT NULL,
    text_ar     VARCHAR(600)         NULL,
    text_nl     VARCHAR(600)         NULL,
    text_fr     VARCHAR(600)         NULL,
    PRIMARY KEY (id),
    KEY idx_dech_q (question_id),
    CONSTRAINT fk_dech_q FOREIGN KEY (question_id)
        REFERENCES dev_exam_questions (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 5. Per-category exam settings ----------------------------------------
CREATE TABLE dev_exam_settings (
    category_id            INT NOT NULL,
    questions_beginner     INT NOT NULL DEFAULT 3,
    questions_intermediate INT NOT NULL DEFAULT 3,
    questions_advanced     INT NOT NULL DEFAULT 3,
    time_limit_minutes     INT NOT NULL DEFAULT 20,
    pass_score_percent     INT NOT NULL DEFAULT 70,
    PRIMARY KEY (category_id),
    CONSTRAINT fk_des_cat FOREIGN KEY (category_id)
        REFERENCES dev_exam_categories (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
