CREATE TABLE IF NOT EXISTS admin_system_settings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    site_name VARCHAR(100) NOT NULL,
    default_language VARCHAR(5) NOT NULL,
    maintenance_mode BIT(1) NOT NULL DEFAULT b'0',
    allow_registrations BIT(1) NOT NULL DEFAULT b'1',
    exam_questions INT NOT NULL DEFAULT 50,
    exam_duration_minutes INT NOT NULL DEFAULT 30,
    passing_score_percent INT NOT NULL DEFAULT 82,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    PRIMARY KEY (id)
);

INSERT INTO admin_system_settings (
    id,
    site_name,
    default_language,
    maintenance_mode,
    allow_registrations,
    exam_questions,
    exam_duration_minutes,
    passing_score_percent,
    created_at,
    updated_at
)
SELECT
    1,
    'ReadyRoad',
    'en',
    b'0',
    b'1',
    50,
    30,
    82,
    NOW(6),
    NOW(6)
WHERE NOT EXISTS (
    SELECT 1 FROM admin_system_settings WHERE id = 1
);
