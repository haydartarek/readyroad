-- ─────────────────────────────────────────────────────────────────────────────
-- V99 — Sign Quiz System
-- New parallel system for sign-specific practice questions and exams.
-- Independent of the legacy quiz_questions / traffic_signs tables.
-- ─────────────────────────────────────────────────────────────────────────────

-- 1. road_signs ────────────────────────────────────────────────────────────────
CREATE TABLE road_signs (
    id                BIGINT        NOT NULL AUTO_INCREMENT,
    sign_code         VARCHAR(50)   NOT NULL,
    category          ENUM(
                          'DANGER','PRIORITY','PROHIBITION','MANDATORY',
                          'PARKING','INFORMATION','ADDITIONAL','ZONE'
                      )             NOT NULL,
    image_path        VARCHAR(500)  NULL,
    serious_violation BOOLEAN       NOT NULL DEFAULT FALSE,
    -- Multilingual sign name
    name_nl           TEXT          NULL,
    name_en           TEXT          NULL,
    name_fr           TEXT          NULL,
    name_ar           TEXT          NULL,
    -- Multilingual sign description
    description_nl    TEXT          NULL,
    description_en    TEXT          NULL,
    description_fr    TEXT          NULL,
    description_ar    TEXT          NULL,
    is_active         BOOLEAN       NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE  KEY uk_road_sign_code   (sign_code),
    INDEX       idx_rs_category     (category),
    INDEX       idx_rs_active       (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 2. sign_questions ────────────────────────────────────────────────────────────
CREATE TABLE sign_questions (
    id              BIGINT  NOT NULL AUTO_INCREMENT,
    sign_id         BIGINT  NOT NULL,
    -- Unique reference key, e.g. "A1_Q01"
    question_ref    VARCHAR(20) NOT NULL,
    question_type   ENUM(
                        'WHAT_DOES_IT_MEAN',
                        'WHICH_SIGN',
                        'WHAT_MUST_YOU_DO',
                        'IS_IT_ALLOWED'
                    ) NOT NULL,
    difficulty      ENUM('EASY','MEDIUM','HARD') NOT NULL,
    is_critical     BOOLEAN NOT NULL DEFAULT FALSE,
    show_sign       BOOLEAN NOT NULL DEFAULT TRUE,
    -- Multilingual question text
    question_nl     TEXT NULL,
    question_en     TEXT NULL,
    question_fr     TEXT NULL,
    question_ar     TEXT NULL,
    -- Multilingual explanation
    explanation_nl  TEXT NULL,
    explanation_en  TEXT NULL,
    explanation_fr  TEXT NULL,
    explanation_ar  TEXT NULL,
    is_active       BOOLEAN   NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE  KEY uk_sq_ref           (question_ref),
    INDEX       idx_sq_sign         (sign_id),
    INDEX       idx_sq_type_diff    (question_type, difficulty),
    INDEX       idx_sq_difficulty   (difficulty),

    CONSTRAINT fk_sq_sign
        FOREIGN KEY (sign_id) REFERENCES road_signs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 3. sign_choices ──────────────────────────────────────────────────────────────
-- Validation rule (enforced in Java Importer):
--   IS_IT_ALLOWED  → exactly 2 choices
--   all other types → exactly 3 choices
CREATE TABLE sign_choices (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    question_id     BIGINT NOT NULL,
    display_order   INT    NOT NULL DEFAULT 1,
    is_correct      BOOLEAN NOT NULL DEFAULT FALSE,
    -- Multilingual choice text
    text_nl         TEXT NULL,
    text_en         TEXT NULL,
    text_fr         TEXT NULL,
    text_ar         TEXT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_sc_q_order     (question_id, display_order),
    INDEX      idx_sc_question   (question_id),
    INDEX      idx_sc_correct    (is_correct),

    CONSTRAINT fk_sc_question
        FOREIGN KEY (question_id) REFERENCES sign_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 4. sign_exams ────────────────────────────────────────────────────────────────
-- Two exams per sign (exam_number 1 and 2).
-- Each exam: 15 questions = 6 EASY + 6 MEDIUM + 3 HARD. Passing score: 12/15.
CREATE TABLE sign_exams (
    id              BIGINT   NOT NULL AUTO_INCREMENT,
    sign_id         BIGINT   NOT NULL,
    exam_number     TINYINT  NOT NULL,       -- 1 or 2
    passing_score   INT      NOT NULL DEFAULT 12,
    total_questions INT      NOT NULL DEFAULT 15,
    easy_count      INT      NOT NULL DEFAULT 6,
    medium_count    INT      NOT NULL DEFAULT 6,
    hard_count      INT      NOT NULL DEFAULT 3,
    is_active       BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_se_sign_num   (sign_id, exam_number),
    INDEX      idx_se_sign      (sign_id),

    CONSTRAINT fk_se_sign
        FOREIGN KEY (sign_id) REFERENCES road_signs(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 5. sign_exam_questions ───────────────────────────────────────────────────────
-- Maps questions to exams. No question overlap between exam_1 and exam_2.
CREATE TABLE sign_exam_questions (
    id              BIGINT NOT NULL AUTO_INCREMENT,
    exam_id         BIGINT NOT NULL,
    question_id     BIGINT NOT NULL,
    question_order  INT    NOT NULL,         -- 1-15
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uk_seq_exam_q    (exam_id, question_id),
    UNIQUE KEY uk_seq_exam_ord  (exam_id, question_order),
    INDEX      idx_seq_exam     (exam_id),
    INDEX      idx_seq_question (question_id),

    CONSTRAINT fk_seq_exam
        FOREIGN KEY (exam_id)     REFERENCES sign_exams(id)     ON DELETE CASCADE,
    CONSTRAINT fk_seq_question
        FOREIGN KEY (question_id) REFERENCES sign_questions(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- 6. sign_import_runs ──────────────────────────────────────────────────────────
-- Audit log for every import execution.
CREATE TABLE sign_import_runs (
    id                  BIGINT        NOT NULL AUTO_INCREMENT,
    performed_by        VARCHAR(100)  NOT NULL DEFAULT 'SYSTEM',
    status              ENUM('SUCCESS','PARTIAL','FAILED') NOT NULL,
    signs_processed     INT           NOT NULL DEFAULT 0,
    signs_created       INT           NOT NULL DEFAULT 0,
    signs_updated       INT           NOT NULL DEFAULT 0,
    signs_skipped       INT           NOT NULL DEFAULT 0,
    questions_created   INT           NOT NULL DEFAULT 0,
    questions_updated   INT           NOT NULL DEFAULT 0,
    exams_created       INT           NOT NULL DEFAULT 0,
    errors_count        INT           NOT NULL DEFAULT 0,
    error_summary       TEXT          NULL,
    duration_ms         BIGINT        NULL,
    created_at          TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    INDEX idx_sir_created_at (created_at DESC),
    INDEX idx_sir_status     (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
