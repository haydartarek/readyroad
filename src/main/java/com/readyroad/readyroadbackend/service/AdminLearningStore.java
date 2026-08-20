package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.CategoryPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.DifficultyPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.DifficultyPerformanceResponse;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ErrorPattern;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ExamSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.LessonActivity;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.PracticeSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.SignPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.StudentSummary;
import java.sql.Timestamp;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminLearningStore {

    private static final String COMPLETED_EXAMS_TEMPLATE = """
            SELECT e.id AS exam_id, e.user_id, 'THEORY_EXAM' AS exam_type,
                   NULL AS subject_code, e.started_at, e.completed_at,
                   e.time_taken_seconds AS duration_seconds,
                   e.total_questions,
                   SUM(CASE WHEN a.answer_state = 'ANSWERED' THEN 1 ELSE 0 END) AS answered_questions,
                   COALESCE(e.correct_answers, 0) AS correct_answers,
                   (SUM(CASE WHEN a.answer_state = 'ANSWERED' THEN 1 ELSE 0 END)
                     - COALESCE(e.correct_answers, 0)) AS incorrect_answers,
                   (e.total_questions
                     - SUM(CASE WHEN a.answer_state = 'ANSWERED' THEN 1 ELSE 0 END)) AS unanswered_answers,
                   COALESCE(e.score_percentage, 0) AS score_percentage,
                   (COALESCE(e.correct_answers, 0) >= 41) AS passed,
                   e.language_code
            FROM exam_simulations e
            LEFT JOIN exam_simulation_answers a ON a.exam_id = e.id
            WHERE e.status = 'COMPLETED'
            GROUP BY e.id
            UNION ALL
            SELECT s.id, s.user_id, 'RANDOM_EXAM', NULL,
                   s.started_at, s.completed_at,
                   %s,
                   s.total_questions, s.answered_count, s.correct_count,
                   (s.answered_count - s.correct_count),
                   (s.total_questions - s.answered_count),
                   COALESCE(s.score_pct, 0),
                   COALESCE(s.passed, FALSE), s.language_code
            FROM sign_random_practice_sessions s
            WHERE s.status = 'COMPLETED'
            UNION ALL
            SELECT r.id, r.user_id, 'TRAFFIC_SIGN_EXAM', r.sign_code,
                   r.completed_at, r.completed_at, NULL,
                   r.total_questions, r.answered_count, r.correct_count,
                   (r.answered_count - r.correct_count),
                   (r.total_questions - r.answered_count),
                   r.score_pct, r.passed, r.language_code
            FROM sign_exam_results r
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;
    private final boolean mysql;

    public AdminLearningStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
        this.mysql = Boolean.TRUE.equals(jdbc.execute((ConnectionCallback<Boolean>) connection ->
                connection.getMetaData().getDatabaseProductName()
                        .toLowerCase(Locale.ROOT)
                        .contains("mysql")));
    }

    public long countStudents(String query) {
        String pattern = searchPattern(query);
        return jdbc.queryForObject("""
                SELECT COUNT(*) FROM users u
                WHERE u.role = 'USER'
                  AND (? = '%%' OR LOWER(COALESCE(u.username, '')) LIKE ?
                    OR LOWER(u.email) LIKE ? OR LOWER(u.full_name) LIKE ?)
                """, Long.class, pattern, pattern, pattern, pattern);
    }

    public List<StudentSummary> findStudents(String query, int limit, int offset) {
        String pattern = searchPattern(query);
        return jdbc.query("""
                WITH completed_exams AS (
                """ + completedExams() + """
                ), ranked_exams AS (
                    SELECT ce.*, ROW_NUMBER() OVER (
                        PARTITION BY ce.user_id ORDER BY ce.completed_at DESC, ce.exam_id DESC
                    ) AS recency_rank
                    FROM completed_exams ce
                ), exam_stats AS (
                    SELECT user_id, COUNT(*) AS exam_count, AVG(score_percentage) AS average_score,
                           MAX(CASE WHEN recency_rank = 1 THEN score_percentage END) AS latest_score,
                           MAX(completed_at) AS latest_exam_at
                    FROM ranked_exams GROUP BY user_id
                ), practice_stats AS (
                    SELECT user_id, COUNT(*) AS practice_count, MAX(completed_at) AS latest_practice_at
                    FROM sign_practice_sessions WHERE status = 'COMPLETED' GROUP BY user_id
                ), lesson_stats AS (
                    SELECT user_id, MAX(last_seen_at) AS latest_lesson_at
                    FROM user_lesson_progress GROUP BY user_id
                ), question_stats AS (
                    SELECT user_id, MAX(CASE
                        WHEN last_presented_at IS NULL THEN answered_at
                        WHEN answered_at IS NULL THEN last_presented_at
                        WHEN last_presented_at >= answered_at THEN last_presented_at
                        ELSE answered_at END) AS latest_question_at
                    FROM user_question_history GROUP BY user_id
                ), activity_events AS (
                    SELECT user_id, latest_exam_at AS activity_at, 'EXAM' AS activity_type
                    FROM exam_stats WHERE latest_exam_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_practice_at, 'PRACTICE'
                    FROM practice_stats WHERE latest_practice_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_lesson_at, 'LESSON'
                    FROM lesson_stats WHERE latest_lesson_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_question_at, 'THEORY_QUESTION'
                    FROM question_stats WHERE latest_question_at IS NOT NULL
                ), latest_activity AS (
                    SELECT user_id, activity_at, activity_type FROM (
                        SELECT ae.*, ROW_NUMBER() OVER (
                            PARTITION BY ae.user_id
                            ORDER BY ae.activity_at DESC,
                                CASE ae.activity_type
                                    WHEN 'EXAM' THEN 1 WHEN 'PRACTICE' THEN 2
                                    WHEN 'LESSON' THEN 3 ELSE 4 END
                        ) AS activity_rank
                        FROM activity_events ae
                    ) ranked_activity WHERE activity_rank = 1
                )
                SELECT u.id, u.username, u.full_name, u.email, u.preferred_language, u.created_at,
                       COALESCE(es.exam_count, 0) AS exam_count,
                       COALESCE(ps.practice_count, 0) AS practice_count,
                       es.average_score, es.latest_score,
                       la.activity_at AS last_active_at,
                       la.activity_type AS last_activity_type
                FROM users u
                LEFT JOIN exam_stats es ON es.user_id = u.id
                LEFT JOIN practice_stats ps ON ps.user_id = u.id
                LEFT JOIN lesson_stats ls ON ls.user_id = u.id
                LEFT JOIN question_stats qs ON qs.user_id = u.id
                LEFT JOIN latest_activity la ON la.user_id = u.id
                WHERE u.role = 'USER'
                  AND (? = '%%' OR LOWER(COALESCE(u.username, '')) LIKE ?
                    OR LOWER(u.email) LIKE ? OR LOWER(u.full_name) LIKE ?)
                ORDER BY u.created_at DESC, u.id DESC
                LIMIT ? OFFSET ?
                """, (rs, rowNum) -> new StudentSummary(
                        rs.getLong("id"), rs.getString("username"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("preferred_language"),
                        localDateTime(rs.getTimestamp("created_at")),
                        localDateTime(rs.getTimestamp("last_active_at")),
                        rs.getLong("exam_count"), rs.getLong("practice_count"),
                        nullableDouble(rs, "average_score"), nullableDouble(rs, "latest_score"),
                        List.of(), List.of(), "INSUFFICIENT_DATA", rs.getString("last_activity_type")),
                pattern, pattern, pattern, pattern, limit, offset);
    }

    public StudentSummary findStudent(long userId) {
        return jdbc.query("""
                WITH completed_exams AS (
                """ + completedExams() + """
                ), ranked_exams AS (
                    SELECT ce.*, ROW_NUMBER() OVER (
                        PARTITION BY ce.user_id ORDER BY ce.completed_at DESC, ce.exam_id DESC
                    ) AS recency_rank
                    FROM completed_exams ce WHERE ce.user_id = ?
                ), exam_stats AS (
                    SELECT user_id, COUNT(*) AS exam_count, AVG(score_percentage) AS average_score,
                           MAX(CASE WHEN recency_rank = 1 THEN score_percentage END) AS latest_score,
                           MAX(completed_at) AS latest_exam_at
                    FROM ranked_exams GROUP BY user_id
                ), practice_stats AS (
                    SELECT user_id, COUNT(*) AS practice_count, MAX(completed_at) AS latest_practice_at
                    FROM sign_practice_sessions WHERE user_id = ? AND status = 'COMPLETED'
                    GROUP BY user_id
                ), lesson_stats AS (
                    SELECT user_id, MAX(last_seen_at) AS latest_lesson_at
                    FROM user_lesson_progress WHERE user_id = ? GROUP BY user_id
                ), question_stats AS (
                    SELECT user_id, MAX(CASE
                        WHEN last_presented_at IS NULL THEN answered_at
                        WHEN answered_at IS NULL THEN last_presented_at
                        WHEN last_presented_at >= answered_at THEN last_presented_at
                        ELSE answered_at END) AS latest_question_at
                    FROM user_question_history WHERE user_id = ? GROUP BY user_id
                ), activity_events AS (
                    SELECT user_id, latest_exam_at AS activity_at, 'EXAM' AS activity_type
                    FROM exam_stats WHERE latest_exam_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_practice_at, 'PRACTICE'
                    FROM practice_stats WHERE latest_practice_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_lesson_at, 'LESSON'
                    FROM lesson_stats WHERE latest_lesson_at IS NOT NULL
                    UNION ALL
                    SELECT user_id, latest_question_at, 'THEORY_QUESTION'
                    FROM question_stats WHERE latest_question_at IS NOT NULL
                ), latest_activity AS (
                    SELECT user_id, activity_at, activity_type FROM (
                        SELECT ae.*, ROW_NUMBER() OVER (
                            PARTITION BY ae.user_id
                            ORDER BY ae.activity_at DESC,
                                CASE ae.activity_type
                                    WHEN 'EXAM' THEN 1 WHEN 'PRACTICE' THEN 2
                                    WHEN 'LESSON' THEN 3 ELSE 4 END
                        ) AS activity_rank
                        FROM activity_events ae
                    ) ranked_activity WHERE activity_rank = 1
                )
                SELECT u.id, u.username, u.full_name, u.email, u.preferred_language, u.created_at,
                       COALESCE(es.exam_count, 0) AS exam_count,
                       COALESCE(ps.practice_count, 0) AS practice_count,
                       es.average_score, es.latest_score,
                       la.activity_at AS last_active_at,
                       la.activity_type AS last_activity_type
                FROM users u
                LEFT JOIN exam_stats es ON es.user_id = u.id
                LEFT JOIN practice_stats ps ON ps.user_id = u.id
                LEFT JOIN lesson_stats ls ON ls.user_id = u.id
                LEFT JOIN question_stats qs ON qs.user_id = u.id
                LEFT JOIN latest_activity la ON la.user_id = u.id
                WHERE u.id = ? AND u.role = 'USER'
                """, rs -> rs.next() ? new StudentSummary(
                        rs.getLong("id"), rs.getString("username"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("preferred_language"),
                        localDateTime(rs.getTimestamp("created_at")), localDateTime(rs.getTimestamp("last_active_at")),
                        rs.getLong("exam_count"), rs.getLong("practice_count"),
                        nullableDouble(rs, "average_score"), nullableDouble(rs, "latest_score"),
                        List.of(), List.of(), "INSUFFICIENT_DATA", rs.getString("last_activity_type")) : null,
                userId, userId, userId, userId, userId);
    }

    public Map<Long, List<Double>> findRecentScores(Set<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        String sql = """
                WITH completed_exams AS (
                """ + completedExams() + """
                ), ranked AS (
                    SELECT user_id, score_percentage,
                           ROW_NUMBER() OVER (PARTITION BY user_id ORDER BY completed_at DESC, exam_id DESC) AS rn
                    FROM completed_exams WHERE user_id IN (:userIds)
                )
                SELECT user_id, score_percentage FROM ranked WHERE rn <= 6 ORDER BY user_id, rn
                """;
        Map<Long, List<Double>> scores = new HashMap<>();
        namedJdbc.query(sql, Map.of("userIds", userIds), (org.springframework.jdbc.core.RowCallbackHandler) rs ->
                scores.computeIfAbsent(rs.getLong("user_id"), ignored -> new ArrayList<>())
                        .add(rs.getDouble("score_percentage")));
        return scores;
    }

    public Map<Long, List<CategoryPerformance>> findCategories(Set<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        String sql = """
                SELECT p.user_id, p.category_id, c.code, c.name_en, c.name_nl, c.name_fr, c.name_ar,
                       p.questions_attempted, p.correct_answers, COALESCE(p.accuracy_rate, 0) AS accuracy_rate,
                       p.last_practiced
                FROM user_category_progress p
                JOIN categories c ON c.id = p.category_id
                WHERE p.user_id IN (:userIds) AND p.questions_attempted > 0
                ORDER BY p.user_id, p.accuracy_rate DESC, p.questions_attempted DESC, c.display_order
                """;
        Map<Long, List<CategoryPerformance>> categories = new HashMap<>();
        namedJdbc.query(sql, Map.of("userIds", userIds), (org.springframework.jdbc.core.RowCallbackHandler) rs -> categories
                .computeIfAbsent(rs.getLong("user_id"), ignored -> new ArrayList<>())
                .add(category(rs)));
        return categories;
    }

    public long countExams(Long userId) {
        String filter = userId == null ? "" : " AND e.user_id = ?";
        String sql = "WITH completed_exams AS (" + completedExams() + ") "
                + "SELECT COUNT(*) FROM completed_exams e JOIN users u ON u.id = e.user_id "
                + "WHERE u.role = 'USER'" + filter;
        return userId == null
                ? jdbc.queryForObject(sql, Long.class)
                : jdbc.queryForObject(sql, Long.class, userId);
    }

    public List<ExamSummary> findExams(Long userId, int limit, int offset) {
        String filter = userId == null ? "" : " AND e.user_id = ?";
        String sql = """
                WITH completed_exams AS (
                """ + completedExams() + """
                )
                SELECT e.*, u.username, u.full_name
                FROM completed_exams e JOIN users u ON u.id = e.user_id
                WHERE u.role = 'USER'
                """ + filter + " ORDER BY e.completed_at DESC, e.exam_id DESC LIMIT ? OFFSET ?";
        var mapper = (org.springframework.jdbc.core.RowMapper<ExamSummary>) (rs, rowNum) -> examSummary(rs);
        return userId == null
                ? jdbc.query(sql, mapper, limit, offset)
                : jdbc.query(sql, mapper, userId, limit, offset);
    }

    public ExamSummary findExam(long userId, String examType, long examId) {
        String sql = "WITH completed_exams AS (" + completedExams() + ") "
                + "SELECT e.*, u.username, u.full_name "
                + "FROM completed_exams e JOIN users u ON u.id = e.user_id "
                + "WHERE u.role = 'USER' AND e.user_id = ? AND e.exam_type = ? AND e.exam_id = ?";
        return jdbc.query(sql, rs -> rs.next() ? examSummary(rs) : null, userId, examType, examId);
    }

    public DifficultyPerformanceResponse findDifficultyPerformance(long userId) {
        String difficultyExpression = mysql
                ? "JSON_UNQUOTE(JSON_EXTRACT(q.historical_snapshot_json, '$.difficulty'))"
                : "q.historical_snapshot_json::jsonb ->> 'difficulty'";
        String evidence = """
                FROM exam_simulation_answers a
                JOIN exam_simulations e ON e.id = a.exam_id
                JOIN exam_simulation_questions q
                  ON q.exam_id = a.exam_id AND q.question_id = a.question_id
                WHERE e.user_id = ? AND e.status = 'COMPLETED'
                  AND a.answer_state = 'ANSWERED'
                """;
        String difficultySql = "SELECT " + difficultyExpression + " AS difficulty,\n"
                + "       COUNT(*) AS answered_questions,\n"
                + "       SUM(CASE WHEN a.is_correct THEN 1 ELSE 0 END) AS correct_answers\n"
                + evidence
                + "  AND q.historical_snapshot_version = 1\n"
                + "  AND q.historical_snapshot_json IS NOT NULL\n"
                + "  AND " + difficultyExpression + " IN ('EASY', 'MEDIUM', 'HARD')\n"
                + "GROUP BY " + difficultyExpression + "\n"
                + "ORDER BY CASE " + difficultyExpression
                + " WHEN 'EASY' THEN 1 WHEN 'MEDIUM' THEN 2 ELSE 3 END";
        List<DifficultyPerformance> items = jdbc.query(difficultySql, (rs, rowNumber) -> {
                    long answered = rs.getLong("answered_questions");
                    long correct = rs.getLong("correct_answers");
                    return new DifficultyPerformance(
                            rs.getString("difficulty"), answered, correct,
                            answered == 0 ? 0 : correct * 100.0 / answered);
                }, userId);

        Map<String, Long> counts = jdbc.query("""
                SELECT SUM(CASE WHEN q.historical_snapshot_version = 1
                           AND q.historical_snapshot_json IS NOT NULL THEN 1 ELSE 0 END) AS snapshot_backed,
                       SUM(CASE WHEN q.historical_snapshot_version IS NULL
                           OR q.historical_snapshot_json IS NULL THEN 1 ELSE 0 END) AS legacy
                """ + evidence, rs -> {
                    if (!rs.next()) return Map.of("snapshot", 0L, "legacy", 0L);
                    return Map.of(
                            "snapshot", rs.getLong("snapshot_backed"),
                            "legacy", rs.getLong("legacy"));
                }, userId);
        long snapshotBacked = counts.get("snapshot");
        long legacy = counts.get("legacy");
        String status = snapshotBacked == 0
                ? legacy == 0 ? "NO_DATA" : "LEGACY_ONLY"
                : legacy == 0 ? "SNAPSHOT_COMPLETE" : "SNAPSHOT_PARTIAL";
        return new DifficultyPerformanceResponse(items, snapshotBacked, legacy, status);
    }

    public long countPractices(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM sign_practice_sessions WHERE user_id = ?",
                Long.class, userId);
    }

    public List<PracticeSummary> findPractices(long userId, int limit, int offset) {
        return jdbc.query("""
                SELECT s.id, s.sign_code, s.status, s.started_at, s.completed_at, s.total_questions,
                       COUNT(a.id) AS answered_count, s.correct_count,
                       (COUNT(a.id) - s.correct_count) AS incorrect_count,
                       CASE WHEN COUNT(a.id) = 0 THEN 0
                            ELSE (s.correct_count * 100.0 / COUNT(a.id)) END AS accuracy,
                       s.language_code
                FROM sign_practice_sessions s
                LEFT JOIN sign_practice_answers a ON a.session_id = s.id
                WHERE s.user_id = ?
                GROUP BY s.id
                ORDER BY s.started_at DESC, s.id DESC LIMIT ? OFFSET ?
                """, (rs, rowNum) -> new PracticeSummary(
                        rs.getLong("id"), rs.getString("sign_code"), rs.getString("status"),
                        localDateTime(rs.getTimestamp("started_at")), localDateTime(rs.getTimestamp("completed_at")),
                        rs.getInt("total_questions"), rs.getInt("answered_count"), rs.getInt("correct_count"),
                        rs.getInt("incorrect_count"), rs.getDouble("accuracy"), rs.getString("language_code")),
                userId, limit, offset);
    }

    public long countLessons(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM user_lesson_progress WHERE user_id = ?",
                Long.class, userId);
    }

    public List<LessonActivity> findLessons(long userId, int limit, int offset) {
        return jdbc.query("""
                SELECT p.lesson_id, l.lesson_code, l.title_en, l.title_nl, l.title_fr, l.title_ar,
                       p.status, p.pages_read, p.created_at, p.last_seen_at, p.completed_at, p.language_code
                FROM user_lesson_progress p JOIN lessons l ON l.id = p.lesson_id
                WHERE p.user_id = ?
                ORDER BY CASE WHEN p.last_seen_at IS NULL THEN 1 ELSE 0 END,
                         p.last_seen_at DESC, p.id DESC LIMIT ? OFFSET ?
                """, (rs, rowNum) -> new LessonActivity(
                        rs.getLong("lesson_id"), rs.getString("lesson_code"), rs.getString("title_en"),
                        rs.getString("title_nl"), rs.getString("title_fr"), rs.getString("title_ar"),
                        rs.getString("status"), rs.getInt("pages_read"),
                        localDateTime(rs.getTimestamp("created_at")), localDateTime(rs.getTimestamp("last_seen_at")),
                        localDateTime(rs.getTimestamp("completed_at")), rs.getString("language_code")),
                userId, limit, offset);
    }

    public List<SignPerformance> findSignPerformance(long userId) {
        return jdbc.query("""
                SELECT r.sign_id, r.sign_code, COUNT(*) AS attempts,
                       SUM(CASE WHEN r.passed THEN 1 ELSE 0 END) AS passed_attempts,
                       AVG(r.score_pct) AS average_score,
                       (SELECT latest.score_pct FROM sign_exam_results latest
                        WHERE latest.user_id = r.user_id AND latest.sign_id = r.sign_id
                        ORDER BY latest.completed_at DESC, latest.id DESC LIMIT 1) AS latest_score,
                       MAX(r.completed_at) AS last_attempt_at
                FROM sign_exam_results r WHERE r.user_id = ?
                GROUP BY r.user_id, r.sign_id, r.sign_code
                ORDER BY average_score ASC, attempts DESC
                """, (rs, rowNum) -> new SignPerformance(
                        rs.getLong("sign_id"), rs.getString("sign_code"), rs.getLong("attempts"),
                        rs.getLong("passed_attempts"), rs.getDouble("average_score"),
                        nullableDouble(rs, "latest_score"), localDateTime(rs.getTimestamp("last_attempt_at"))), userId);
    }

    public long countErrorPatterns(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM user_error_patterns WHERE user_id = ?",
                Long.class, userId);
    }

    public List<ErrorPattern> findErrorPatterns(long userId, int limit, int offset) {
        return jdbc.query("""
                SELECT id, error_type, question_type, question_ref_id, traffic_sign_code,
                       rule_category, occurred_at
                FROM user_error_patterns WHERE user_id = ?
                ORDER BY occurred_at DESC, id DESC LIMIT ? OFFSET ?
                """, (rs, rowNum) -> new ErrorPattern(
                        rs.getLong("id"), rs.getString("error_type"), rs.getString("question_type"),
                        nullableLong(rs, "question_ref_id"), rs.getString("traffic_sign_code"),
                        rs.getString("rule_category"), localDateTime(rs.getTimestamp("occurred_at"))),
                userId, limit, offset);
    }

    private static String searchPattern(String query) {
        return query == null || query.isBlank() ? "%%" : "%" + query.trim().toLowerCase() + "%";
    }

    private String completedExams() {
        String randomExamDuration = mysql
                ? "TIMESTAMPDIFF(SECOND, s.started_at, s.completed_at)"
                : "EXTRACT(EPOCH FROM (s.completed_at - s.started_at))";
        return COMPLETED_EXAMS_TEMPLATE.formatted(randomExamDuration);
    }

    private static CategoryPerformance category(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new CategoryPerformance(
                rs.getLong("category_id"), rs.getString("code"), rs.getString("name_en"),
                rs.getString("name_nl"), rs.getString("name_fr"), rs.getString("name_ar"),
                rs.getInt("questions_attempted"), rs.getInt("correct_answers"),
                rs.getDouble("accuracy_rate"), localDateTime(rs.getTimestamp("last_practiced")));
    }

    private static ExamSummary examSummary(ResultSet rs) throws SQLException {
        return new ExamSummary(
                rs.getLong("exam_id"), rs.getLong("user_id"), rs.getString("username"),
                rs.getString("full_name"), rs.getString("exam_type"), rs.getString("subject_code"),
                localDateTime(rs.getTimestamp("started_at")), localDateTime(rs.getTimestamp("completed_at")),
                nullableLong(rs, "duration_seconds"), rs.getInt("total_questions"),
                rs.getInt("answered_questions"), rs.getInt("correct_answers"),
                rs.getInt("incorrect_answers"), rs.getInt("unanswered_answers"),
                rs.getDouble("score_percentage"), rs.getBoolean("passed"), rs.getString("language_code"));
    }

    private static LocalDateTime localDateTime(Timestamp value) {
        return value == null ? null : value.toLocalDateTime();
    }

    private static Double nullableDouble(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    private static Long nullableLong(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}
