ALTER TABLE admin_system_settings
    ALTER COLUMN exam_duration_minutes TYPE NUMERIC(5, 2)
    USING exam_duration_minutes::NUMERIC(5, 2);

ALTER TABLE admin_system_settings
    ALTER COLUMN exam_duration_minutes SET DEFAULT 12.50;

UPDATE admin_system_settings
SET exam_duration_minutes = 12.50
WHERE exam_duration_minutes = 30.00;
