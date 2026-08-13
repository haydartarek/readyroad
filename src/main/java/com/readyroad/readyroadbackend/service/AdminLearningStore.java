package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.CategoryPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ErrorPattern;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.ExamSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.LessonActivity;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.PracticeSummary;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.SignPerformance;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.StudentSummary;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminLearningStore {

    private static final String COMPLETED_EXAMS = """
            SELECT e.id AS exam_id, e.user_id, 'THEORY_EXAM' AS exam_type,
                   NULL::varchar AS subject_code, e.started_at, e.completed_at,
                   e.time_taken_seconds::bigint AS duration_seconds,
                   e.total_questions, COUNT(a.id)::int AS answered_questions,
                   COALESCE(e.correct_answers, 0)::int AS correct_answers,
                   (COUNT(a.id) - COALESCE(e.correct_answers, 0))::int AS incorrect_answers,
                   (e.total_questions - COUNT(a.id))::int AS unanswered_answers,
                   COALESCE(e.score_percentage, 0)::double precision AS score_percentage,
                   (COALESCE(e.correct_answers, 0) >= 41) AS passed,
                   e.language_code
            FROM exam_simulations e
            LEFT JOIN exam_simulation_answers a ON a.exam_id = e.id
            WHERE e.status = 'COMPLETED'
            GROUP BY e.id
            UNION ALL
            SELECT s.id, s.user_id, 'RANDOM_EXAM', NULL::varchar,
                   s.started_at, s.completed_at,
                   EXTRACT(EPOCH FROM (s.completed_at - s.started_at))::bigint,
                   s.total_questions, s.answered_count, s.correct_count,
                   (s.answered_count - s.correct_count),
                   (s.total_questions - s.answered_count),
                   COALESCE(s.score_pct, 0)::double precision,
                   COALESCE(s.passed, false), s.language_code
            FROM sign_random_practice_sessions s
            WHERE s.status = 'COMPLETED'
            UNION ALL
            SELECT r.id, r.user_id, 'TRAFFIC_SIGN_EXAM', r.sign_code,
                   r.completed_at, r.completed_at, NULL::bigint,
                   r.total_questions, r.answered_count, r.correct_count,
                   (r.answered_count - r.correct_count),
                   (r.total_questions - r.answered_count),
                   r.score_pct::double precision, r.passed, r.language_code
            FROM sign_exam_results r
            """;

    private final JdbcTemplate jdbc;
    private final NamedParameterJdbcTemplate namedJdbc;

    public AdminLearningStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
        this.namedJdbc = new NamedParameterJdbcTemplate(jdbc);
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
                """ + COMPLETED_EXAMS + """
                ), exam_stats AS (
                    SELECT user_id, COUNT(*) AS exam_count, AVG(score_percentage) AS average_score,
                           (ARRAY_AGG(score_percentage ORDER BY completed_at DESC))[1] AS latest_score,
                           MAX(completed_at) AS latest_exam_at
                    FROM completed_exams GROUP BY user_id
                ), practice_stats AS (
                    SELECT user_id, COUNT(*) AS practice_count, MAX(completed_at) AS latest_practice_at
                    FROM sign_practice_sessions WHERE status = 'COMPLETED' GROUP BY user_id
                ), lesson_stats AS (
                    SELECT user_id, MAX(last_seen_at) AS latest_lesson_at
                    FROM user_lesson_progress GROUP BY user_id
                )
                SELECT u.id, u.username, u.full_name, u.email, u.preferred_language, u.created_at,
                       COALESCE(es.exam_count, 0) AS exam_count,
                       COALESCE(ps.practice_count, 0) AS practice_count,
                       es.average_score, es.latest_score,
                       GREATEST(es.latest_exam_at, ps.latest_practice_at, ls.latest_lesson_at) AS last_active_at,
                       CASE GREATEST(es.latest_exam_at, ps.latest_practice_at, ls.latest_lesson_at)
                         WHEN es.latest_exam_at THEN 'EXAM'
                         WHEN ps.latest_practice_at THEN 'PRACTICE'
                         WHEN ls.latest_lesson_at THEN 'LESSON'
                         ELSE NULL END AS last_activity_type
                FROM users u
                LEFT JOIN exam_stats es ON es.user_id = u.id
                LEFT JOIN practice_stats ps ON ps.user_id = u.id
                LEFT JOIN lesson_stats ls ON ls.user_id = u.id
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
                """ + COMPLETED_EXAMS + """
                ), exam_stats AS (
                    SELECT COUNT(*) AS exam_count, AVG(score_percentage) AS average_score,
                           (ARRAY_AGG(score_percentage ORDER BY completed_at DESC))[1] AS latest_score,
                           MAX(completed_at) AS latest_exam_at
                    FROM completed_exams WHERE user_id = ?
                ), practice_stats AS (
                    SELECT COUNT(*) AS practice_count, MAX(completed_at) AS latest_practice_at
                    FROM sign_practice_sessions WHERE user_id = ? AND status = 'COMPLETED'
                ), lesson_stats AS (
                    SELECT MAX(last_seen_at) AS latest_lesson_at
                    FROM user_lesson_progress WHERE user_id = ?
                )
                SELECT u.id, u.username, u.full_name, u.email, u.preferred_language, u.created_at,
                       es.exam_count, ps.practice_count, es.average_score, es.latest_score,
                       GREATEST(es.latest_exam_at, ps.latest_practice_at, ls.latest_lesson_at) AS last_active_at,
                       CASE GREATEST(es.latest_exam_at, ps.latest_practice_at, ls.latest_lesson_at)
                         WHEN es.latest_exam_at THEN 'EXAM'
                         WHEN ps.latest_practice_at THEN 'PRACTICE'
                         WHEN ls.latest_lesson_at THEN 'LESSON'
                         ELSE NULL END AS last_activity_type
                FROM users u CROSS JOIN exam_stats es CROSS JOIN practice_stats ps CROSS JOIN lesson_stats ls
                WHERE u.id = ? AND u.role = 'USER'
                """, rs -> rs.next() ? new StudentSummary(
                        rs.getLong("id"), rs.getString("username"), rs.getString("full_name"),
                        rs.getString("email"), rs.getString("preferred_language"),
                        localDateTime(rs.getTimestamp("created_at")), localDateTime(rs.getTimestamp("last_active_at")),
                        rs.getLong("exam_count"), rs.getLong("practice_count"),
                        nullableDouble(rs, "average_score"), nullableDouble(rs, "latest_score"),
                        List.of(), List.of(), "INSUFFICIENT_DATA", rs.getString("last_activity_type")) : null,
                userId, userId, userId, userId);
    }

    public Map<Long, List<Double>> findRecentScores(Set<Long> userIds) {
        if (userIds.isEmpty()) return Map.of();
        String sql = """
                WITH completed_exams AS (
                """ + COMPLETED_EXAMS + """
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
        String sql = "WITH completed_exams AS (" + COMPLETED_EXAMS + ") "
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
                """ + COMPLETED_EXAMS + """
                )
                SELECT e.*, u.username, u.full_name
                FROM completed_exams e JOIN users u ON u.id = e.user_id
                WHERE u.role = 'USER'
                """ + filter + " ORDER BY e.completed_at DESC, e.exam_id DESC LIMIT ? OFFSET ?";
        var mapper = (org.springframework.jdbc.core.RowMapper<ExamSummary>) (rs, rowNum) -> new ExamSummary(
                        rs.getLong("exam_id"), rs.getLong("user_id"), rs.getString("username"),
                        rs.getString("full_name"), rs.getString("exam_type"), rs.getString("subject_code"),
                        localDateTime(rs.getTimestamp("started_at")), localDateTime(rs.getTimestamp("completed_at")),
                        nullableLong(rs, "duration_seconds"), rs.getInt("total_questions"),
                        rs.getInt("answered_questions"), rs.getInt("correct_answers"),
                        rs.getInt("incorrect_answers"), rs.getInt("unanswered_answers"),
                        rs.getDouble("score_percentage"), rs.getBoolean("passed"), rs.getString("language_code"));
        return userId == null
                ? jdbc.query(sql, mapper, limit, offset)
                : jdbc.query(sql, mapper, userId, limit, offset);
    }

    public long countPractices(long userId) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM sign_practice_sessions WHERE user_id = ?",
                Long.class, userId);
    }

    public List<PracticeSummary> findPractices(long userId, int limit, int offset) {
        return jdbc.query("""
                SELECT s.id, s.sign_code, s.status, s.started_at, s.completed_at, s.total_questions,
                       COUNT(a.id)::int AS answered_count, s.correct_count,
                       (COUNT(a.id) - s.correct_count)::int AS incorrect_count,
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
                WHERE p.user_id = ? ORDER BY p.last_seen_at DESC NULLS LAST, p.id DESC LIMIT ? OFFSET ?
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
                SELECT sign_id, sign_code, COUNT(*) AS attempts,
                       COUNT(*) FILTER (WHERE passed) AS passed_attempts,
                       AVG(score_pct) AS average_score,
                       (ARRAY_AGG(score_pct ORDER BY completed_at DESC, id DESC))[1] AS latest_score,
                       MAX(completed_at) AS last_attempt_at
                FROM sign_exam_results WHERE user_id = ?
                GROUP BY sign_id, sign_code ORDER BY average_score ASC, attempts DESC
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

    private static CategoryPerformance category(java.sql.ResultSet rs) throws java.sql.SQLException {
        return new CategoryPerformance(
                rs.getLong("category_id"), rs.getString("code"), rs.getString("name_en"),
                rs.getString("name_nl"), rs.getString("name_fr"), rs.getString("name_ar"),
                rs.getInt("questions_attempted"), rs.getInt("correct_answers"),
                rs.getDouble("accuracy_rate"), localDateTime(rs.getTimestamp("last_practiced")));
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
