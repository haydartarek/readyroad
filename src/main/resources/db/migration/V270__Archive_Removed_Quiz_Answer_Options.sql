-- Align the MySQL quiz option schema with the historical-safety contract.
-- The standalone question index must exist before the legacy unique index is
-- removed because InnoDB also uses that legacy index for the foreign key.
SET @add_question_index = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE quiz_answer_options ADD INDEX idx_quiz_answer_options_question_id (question_id)',
        'SELECT 1'
    )
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'quiz_answer_options'
      AND index_name = 'idx_quiz_answer_options_question_id'
);
PREPARE add_question_index_statement FROM @add_question_index;
EXECUTE add_question_index_statement;
DEALLOCATE PREPARE add_question_index_statement;

-- This guard also makes a failed local DDL attempt safely repairable. It does
-- not alter the normal one-time production migration path.
SET @add_is_active = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE quiz_answer_options ADD COLUMN is_active BOOLEAN NOT NULL DEFAULT TRUE AFTER display_order',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'quiz_answer_options'
      AND column_name = 'is_active'
);
PREPARE add_is_active_statement FROM @add_is_active;
EXECUTE add_is_active_statement;
DEALLOCATE PREPARE add_is_active_statement;

ALTER TABLE quiz_answer_options
    DROP INDEX uq_question_display_order;

-- MySQL has no partial unique indexes. NULL generated values allow archived
-- options to retain their original order while active options remain unique.
ALTER TABLE quiz_answer_options
    ADD COLUMN active_display_order INT
        GENERATED ALWAYS AS (CASE WHEN is_active THEN display_order ELSE NULL END) STORED
        AFTER is_active,
    ADD UNIQUE INDEX uq_question_active_display_order (question_id, active_display_order),
    ADD INDEX idx_quiz_answer_options_active_question (question_id, is_active);
