CREATE VIEW user_quiz_stats AS
SELECT
    u.id AS user_id,
    u.email,
    u.full_name,
    COUNT(qa.id) AS total_attempts,
    AVG(qa.score_percentage) AS avg_score,
    SUM(CASE WHEN qa.passed THEN 1 ELSE 0 END) AS passed_exams,
    MAX(qa.completed_at) AS last_attempt
FROM users u
LEFT JOIN quiz_attempts qa ON qa.user_id = u.id
WHERE u.is_active = TRUE
GROUP BY u.id, u.email, u.full_name;

CREATE VIEW weak_areas_summary AS
SELECT
    uwa.user_id,
    uwa.category,
    AVG(uwa.accuracy_percentage) AS avg_accuracy,
    COUNT(*) AS question_count
FROM user_weak_areas uwa
WHERE uwa.accuracy_percentage < 80.00
GROUP BY uwa.user_id, uwa.category
HAVING COUNT(*) >= 3;
