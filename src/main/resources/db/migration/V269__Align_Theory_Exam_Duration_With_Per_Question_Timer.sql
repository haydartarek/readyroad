ALTER TABLE admin_system_settings
    MODIFY COLUMN exam_duration_minutes DECIMAL(5, 2) NOT NULL DEFAULT 12.50;

UPDATE admin_system_settings
SET exam_duration_minutes = 12.50
WHERE exam_duration_minutes = 30.00;
