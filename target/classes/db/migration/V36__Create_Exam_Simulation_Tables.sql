-- V36: Create Exam Simulation Tables
-- Phase 5: Story A1, A2, A3 - Exam simulation system
-- Date: 2026-01-20

-- ============================================================================
-- 1. Main Exam Simulations Table
-- ============================================================================

CREATE TABLE exam_simulations (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    total_questions INT NOT NULL DEFAULT 50,
    correct_answers INT NULL,
    score_percentage DECIMAL(5,2) NULL,
    time_taken_seconds INT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_exam_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT chk_exam_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'ABANDONED', 'EXPIRED')),
    CONSTRAINT chk_exam_total_questions CHECK (total_questions = 50),
    CONSTRAINT chk_exam_correct_answers CHECK (correct_answers IS NULL OR (correct_answers >= 0 AND correct_answers <= 50)),
    CONSTRAINT chk_exam_score CHECK (score_percentage IS NULL OR (score_percentage >= 0 AND score_percentage <= 100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Performance indexes
CREATE INDEX idx_exam_user_started ON exam_simulations(user_id, started_at DESC);
CREATE INDEX idx_exam_status_completed ON exam_simulations(status, completed_at DESC);
CREATE INDEX idx_exam_user_status ON exam_simulations(user_id, status);

-- ============================================================================
-- 2. Exam Simulation Questions (Junction Table)
-- ============================================================================

CREATE TABLE exam_simulation_questions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    question_order INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_esq_exam FOREIGN KEY (exam_id) REFERENCES exam_simulations(id) ON DELETE CASCADE,
    CONSTRAINT fk_esq_question FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    CONSTRAINT chk_esq_order CHECK (question_order >= 1 AND question_order <= 50),
    CONSTRAINT uq_exam_question_order UNIQUE (exam_id, question_order),
    CONSTRAINT uq_exam_question UNIQUE (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optimize question retrieval
CREATE INDEX idx_exam_q_order ON exam_simulation_questions(exam_id, question_order ASC);

-- ============================================================================
-- 3. Exam Simulation Answers
-- ============================================================================

CREATE TABLE exam_simulation_answers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    exam_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    selected_option_id BIGINT NOT NULL,
    is_correct BOOLEAN NOT NULL DEFAULT FALSE,
    time_taken_seconds INT NOT NULL DEFAULT 0,
    answered_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    CONSTRAINT fk_esa_exam FOREIGN KEY (exam_id) REFERENCES exam_simulations(id) ON DELETE CASCADE,
    CONSTRAINT fk_esa_question FOREIGN KEY (question_id) REFERENCES quiz_questions(id) ON DELETE CASCADE,
    CONSTRAINT fk_esa_option FOREIGN KEY (selected_option_id) REFERENCES quiz_answer_options(id) ON DELETE CASCADE,
    CONSTRAINT chk_esa_time CHECK (time_taken_seconds >= 0 AND time_taken_seconds <= 1800),
    CONSTRAINT uq_exam_answer UNIQUE (exam_id, question_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Optimize answer analytics
CREATE INDEX idx_exam_ans_correct ON exam_simulation_answers(exam_id, is_correct);
CREATE INDEX idx_exam_ans_time ON exam_simulation_answers(exam_id, time_taken_seconds);
CREATE INDEX idx_exam_ans_question ON exam_simulation_answers(question_id, is_correct);
