-- Port the existing PostgreSQL correct-option snapshot to the local MySQL
-- migration stream so historical exam answers use the same entity contract.

SET @add_correct_option_column = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE exam_simulation_answers ADD COLUMN correct_option_id BIGINT NULL AFTER selected_option_id',
        'SELECT 1'
    )
    FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'exam_simulation_answers'
      AND column_name = 'correct_option_id'
);
PREPARE add_correct_option_column_stmt FROM @add_correct_option_column;
EXECUTE add_correct_option_column_stmt;
DEALLOCATE PREPARE add_correct_option_column_stmt;

UPDATE exam_simulation_answers
SET correct_option_id = selected_option_id
WHERE correct_option_id IS NULL
  AND is_correct = TRUE;

UPDATE exam_simulation_answers answer
SET correct_option_id = (
    SELECT option_row.id
    FROM quiz_answer_options option_row
    WHERE option_row.question_id = answer.question_id
      AND option_row.is_correct = TRUE
    ORDER BY
      option_row.display_order,
      option_row.id
    LIMIT 1
)
WHERE answer.correct_option_id IS NULL;

SET @add_correct_option_fk = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE exam_simulation_answers ADD CONSTRAINT fk_esa_correct_option FOREIGN KEY (correct_option_id) REFERENCES quiz_answer_options (id) ON DELETE SET NULL',
        'SELECT 1'
    )
    FROM information_schema.table_constraints
    WHERE constraint_schema = DATABASE()
      AND table_name = 'exam_simulation_answers'
      AND constraint_name = 'fk_esa_correct_option'
);
PREPARE add_correct_option_fk_stmt FROM @add_correct_option_fk;
EXECUTE add_correct_option_fk_stmt;
DEALLOCATE PREPARE add_correct_option_fk_stmt;

SET @add_correct_option_index = (
    SELECT IF(
        COUNT(*) = 0,
        'CREATE INDEX idx_exam_answers_correct_option ON exam_simulation_answers (correct_option_id)',
        'SELECT 1'
    )
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'exam_simulation_answers'
      AND index_name = 'idx_exam_answers_correct_option'
);
PREPARE add_correct_option_index_stmt FROM @add_correct_option_index;
EXECUTE add_correct_option_index_stmt;
DEALLOCATE PREPARE add_correct_option_index_stmt;
