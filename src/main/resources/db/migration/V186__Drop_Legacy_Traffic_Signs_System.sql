SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS traffic_sign_rules;
DROP TABLE IF EXISTS traffic_sign_details;

-- quiz_questions: drop FK if exists, drop column if exists
SET @fk1 = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_questions'
    AND CONSTRAINT_NAME = 'quiz_questions_ibfk_2' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql = IF(@fk1 > 0, 'ALTER TABLE quiz_questions DROP FOREIGN KEY quiz_questions_ibfk_2', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @col1 = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'quiz_questions' AND COLUMN_NAME = 'traffic_sign_id');
SET @sql = IF(@col1 > 0, 'ALTER TABLE quiz_questions DROP COLUMN traffic_sign_id', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- user_error_patterns: drop FK if exists, drop column if exists
SET @fk2 = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'user_error_patterns'
    AND CONSTRAINT_NAME = 'user_error_patterns_ibfk_2' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql = IF(@fk2 > 0, 'ALTER TABLE user_error_patterns DROP FOREIGN KEY user_error_patterns_ibfk_2', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @col2 = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_error_patterns' AND COLUMN_NAME = 'traffic_sign_id');
SET @sql = IF(@col2 > 0, 'ALTER TABLE user_error_patterns DROP COLUMN traffic_sign_id', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- user_weak_areas: drop FK if exists, drop column if exists
SET @fk3 = (SELECT COUNT(*) FROM information_schema.TABLE_CONSTRAINTS
    WHERE CONSTRAINT_SCHEMA = DATABASE() AND TABLE_NAME = 'user_weak_areas'
    AND CONSTRAINT_NAME = 'user_weak_areas_ibfk_3' AND CONSTRAINT_TYPE = 'FOREIGN KEY');
SET @sql = IF(@fk3 > 0, 'ALTER TABLE user_weak_areas DROP FOREIGN KEY user_weak_areas_ibfk_3', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @col3 = (SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'user_weak_areas' AND COLUMN_NAME = 'traffic_sign_id');
SET @sql = IF(@col3 > 0, 'ALTER TABLE user_weak_areas DROP COLUMN traffic_sign_id', 'SELECT 1');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

DROP TABLE IF EXISTS traffic_signs;

SET FOREIGN_KEY_CHECKS = 1;
