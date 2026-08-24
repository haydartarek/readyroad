package com.readyroad.readyroadbackend.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AdminTheoryBankHealthStore {

    private final JdbcTemplate jdbc;

    public AdminTheoryBankHealthStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Map<Long, Long> theoryPresentations() {
        String sql = """
                SELECT h.question_ref_id,
                       COALESCE(SUM(h.times_presented), 0) AS presentations
                FROM user_question_history h
                JOIN users u ON u.id = h.user_id AND u.role = 'USER'
                WHERE h.question_type = 'THEORY'
                GROUP BY h.question_ref_id
                """;
        Map<Long, Long> result = new HashMap<>();
        jdbc.query(sql, (org.springframework.jdbc.core.RowCallbackHandler) rs ->
                result.put(rs.getLong("question_ref_id"), rs.getLong("presentations")));
        return result;
    }

    public Map<Long, Map<String, PerformanceRow>> completedPerformanceByLocale() {
        String sql = """
                SELECT a.question_id,
                       COALESCE(NULLIF(LOWER(e.language_code), ''), 'en') AS locale,
                       COUNT(*) AS answered,
                       SUM(CASE WHEN a.is_correct = true THEN 1 ELSE 0 END) AS correct,
                       AVG(a.time_taken_seconds) AS average_answer_time
                FROM exam_simulation_answers a
                JOIN exam_simulations e ON e.id = a.exam_id AND e.status = 'COMPLETED'
                JOIN users u ON u.id = e.user_id AND u.role = 'USER'
                WHERE a.answer_state = 'ANSWERED'
                GROUP BY a.question_id, COALESCE(NULLIF(LOWER(e.language_code), ''), 'en')
                ORDER BY a.question_id, locale
                """;
        Map<Long, Map<String, PerformanceRow>> result = new LinkedHashMap<>();
        jdbc.query(sql, (org.springframework.jdbc.core.RowCallbackHandler) rs -> result
                .computeIfAbsent(rs.getLong("question_id"), ignored -> new LinkedHashMap<>())
                .put(rs.getString("locale"), new PerformanceRow(
                        rs.getLong("answered"),
                        rs.getLong("correct"),
                        nullableDouble(rs, "average_answer_time"))));
        return result;
    }

    private static Double nullableDouble(java.sql.ResultSet rs, String column)
            throws java.sql.SQLException {
        double value = rs.getDouble(column);
        return rs.wasNull() ? null : value;
    }

    public record PerformanceRow(long answered, long correct, Double averageAnswerTimeSeconds) {
    }
}
