package com.readyroad.readyroadbackend.domain.repository;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/** Database-portable read model for current localized THEORY-bank coverage. */
@Repository
public class TheoryQuestionCoverageStore {

    private static final Map<String, LanguageColumns> LANGUAGE_COLUMNS = Map.of(
            "ar", new LanguageColumns("question_ar", "option_text_ar", "name_ar"),
            "nl", new LanguageColumns("question_nl", "option_text_nl", "name_nl"),
            "fr", new LanguageColumns("question_fr", "option_text_fr", "name_fr"),
            "en", new LanguageColumns("question_en", "option_text_en", "name_en"));

    private final JdbcTemplate jdbc;

    public TheoryQuestionCoverageStore(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<CoverageRow> findCoverage(long userId, String languageCode) {
        LanguageColumns columns = columns(languageCode);
        String questionText = "q." + columns.questionText();
        String optionText = "o." + columns.optionText();
        String categoryName = "c." + columns.categoryName();

        String sql = """
                WITH eligible_questions AS (
                    SELECT q.id AS question_id, q.category_id
                    FROM quiz_questions q
                    JOIN categories c ON c.id = q.category_id
                    JOIN quiz_answer_options o
                      ON o.question_id = q.id
                     AND o.is_active = true
                    WHERE q.is_active = true
                      AND q.status = 'PUBLISHED'
                      AND c.is_active = true
                      AND c.content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                      AND %s
                    GROUP BY q.id, q.category_id
                    HAVING COUNT(o.id) BETWEEN 2 AND 3
                       AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1
                       AND SUM(CASE WHEN %s THEN 0 ELSE 1 END) = 0
                )
                SELECT c.id AS category_id,
                       c.code AS category_code,
                       COALESCE(NULLIF(TRIM(%s), ''), NULLIF(TRIM(c.name_en), ''), c.code)
                           AS category_name,
                       COUNT(e.question_id) AS eligible_questions,
                        COALESCE(SUM(CASE
                            WHEN COALESCE(h.times_presented, 0) > 0 THEN 1 ELSE 0 END), 0)
                            AS unique_questions_seen,
                        COALESCE(SUM(CASE
                            WHEN COALESCE(h.times_correct, 0) + COALESCE(h.times_wrong, 0) > 0
                            THEN 1 ELSE 0 END), 0) AS unique_questions_answered,
                       COALESCE(SUM(COALESCE(h.times_presented, 0)), 0) AS times_presented,
                       COALESCE(SUM(COALESCE(h.times_correct, 0) + COALESCE(h.times_wrong, 0)), 0)
                           AS times_answered,
                       COALESCE(SUM(COALESCE(h.times_correct, 0)), 0) AS times_correct,
                       COALESCE(SUM(COALESCE(h.times_wrong, 0)), 0) AS times_incorrect
                FROM categories c
                LEFT JOIN eligible_questions e ON e.category_id = c.id
                LEFT JOIN user_question_history h
                  ON h.user_id = ?
                 AND h.question_ref_id = e.question_id
                 AND h.question_type = 'THEORY'
                WHERE c.is_active = true
                  AND c.content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                GROUP BY c.id, c.code, %s, c.name_en, c.display_order
                ORDER BY COALESCE(c.display_order, 2147483647), c.id
                """.formatted(
                usableText(questionText),
                usableText(optionText),
                categoryName,
                categoryName);

        return jdbc.query(sql, (rs, rowNumber) -> new CoverageRow(
                rs.getLong("category_id"),
                rs.getString("category_code"),
                rs.getString("category_name"),
                rs.getLong("eligible_questions"),
                rs.getLong("unique_questions_seen"),
                rs.getLong("unique_questions_answered"),
                rs.getLong("times_presented"),
                rs.getLong("times_answered"),
                rs.getLong("times_correct"),
                rs.getLong("times_incorrect")), userId);
    }

    private static LanguageColumns columns(String languageCode) {
        String normalized = languageCode == null ? "" : languageCode.trim().toLowerCase(Locale.ROOT);
        LanguageColumns columns = LANGUAGE_COLUMNS.get(normalized);
        if (columns == null) {
            throw new IllegalArgumentException("Unsupported coverage language");
        }
        return columns;
    }

    private static String usableText(String column) {
        return column + " IS NOT NULL"
                + " AND TRIM(" + column + ") <> ''"
                + " AND LOWER(TRIM(" + column + ")) NOT IN"
                + " ('option a', 'option b', 'option c', 'optie a', 'optie b', 'optie c')"
                + " AND POSITION('??' IN " + column + ") = 0";
    }

    private record LanguageColumns(String questionText, String optionText, String categoryName) {
    }

    public record CoverageRow(
            long categoryId,
            String categoryCode,
            String categoryName,
            long eligibleQuestions,
            long uniqueQuestionsSeen,
            long uniqueQuestionsAnswered,
            long timesPresented,
            long timesAnswered,
            long timesCorrect,
            long timesIncorrect) {
    }
}
