package com.readyroad.readyroadbackend.domain.repository.custom;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

public class UserWeakAreaUpsertRepositoryImpl implements UserWeakAreaUpsertRepository {

    private static final String SIGN_INSERT = """
            INSERT INTO user_weak_areas
                (user_id, traffic_sign_code, total_questions, correct_answers, wrong_answers,
                 accuracy_percentage, last_updated)
            VALUES
                (:userId, :keyValue, :totalQ, :correct, :wrong,
                 CASE WHEN :totalQ > 0 THEN (:correct * 100.0 / :totalQ) ELSE 0.0 END,
                 CURRENT_TIMESTAMP)
            """;

    private static final String CATEGORY_INSERT = """
            INSERT INTO user_weak_areas
                (user_id, category, total_questions, correct_answers, wrong_answers,
                 accuracy_percentage, last_updated)
            VALUES
                (:userId, :keyValue, :totalQ, :correct, :wrong,
                 CASE WHEN :totalQ > 0 THEN (:correct * 100.0 / :totalQ) ELSE 0.0 END,
                 CURRENT_TIMESTAMP)
            """;

    private static final String MYSQL_SIGN_UPSERT = SIGN_INSERT + mysqlUpdateClause();
    private static final String MYSQL_CATEGORY_UPSERT = CATEGORY_INSERT + mysqlUpdateClause();
    private static final String POSTGRESQL_SIGN_UPSERT = SIGN_INSERT
            + postgresqlUpdateClause("user_id, traffic_sign_code");
    private static final String POSTGRESQL_CATEGORY_UPSERT = CATEGORY_INSERT
            + postgresqlUpdateClause("user_id, category");

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DatabaseDialectResolver dialectResolver;

    public UserWeakAreaUpsertRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dialectResolver = new DatabaseDialectResolver(dataSource);
    }

    @Override
    @Transactional
    public void upsertBySignCode(Long userId, String signCode, int totalQ, int correct, int wrong) {
        jdbcTemplate.update(isPostgreSql() ? POSTGRESQL_SIGN_UPSERT : MYSQL_SIGN_UPSERT,
                parameters(userId, signCode, totalQ, correct, wrong));
    }

    @Override
    @Transactional
    public void upsertByCategoryName(Long userId, String category, int totalQ, int correct, int wrong) {
        jdbcTemplate.update(isPostgreSql() ? POSTGRESQL_CATEGORY_UPSERT : MYSQL_CATEGORY_UPSERT,
                parameters(userId, category, totalQ, correct, wrong));
    }

    private static String mysqlUpdateClause() {
        // Accuracy is assigned before counters because MySQL evaluates assignments left-to-right.
        return """
                ON DUPLICATE KEY UPDATE
                    accuracy_percentage = CASE
                        WHEN total_questions + :totalQ > 0
                        THEN (correct_answers + :correct) * 100.0 / (total_questions + :totalQ)
                        ELSE 0.0
                    END,
                    total_questions = total_questions + :totalQ,
                    correct_answers = correct_answers + :correct,
                    wrong_answers = wrong_answers + :wrong,
                    last_updated = CURRENT_TIMESTAMP
                """;
    }

    private static String postgresqlUpdateClause(String conflictTarget) {
        return """
                ON CONFLICT (%s) DO UPDATE SET
                    accuracy_percentage = CASE
                        WHEN user_weak_areas.total_questions + EXCLUDED.total_questions > 0
                        THEN (user_weak_areas.correct_answers + EXCLUDED.correct_answers) * 100.0
                             / (user_weak_areas.total_questions + EXCLUDED.total_questions)
                        ELSE 0.0
                    END,
                    total_questions = user_weak_areas.total_questions + EXCLUDED.total_questions,
                    correct_answers = user_weak_areas.correct_answers + EXCLUDED.correct_answers,
                    wrong_answers = user_weak_areas.wrong_answers + EXCLUDED.wrong_answers,
                    last_updated = CURRENT_TIMESTAMP
                """.formatted(conflictTarget);
    }

    private MapSqlParameterSource parameters(
            Long userId,
            String keyValue,
            int totalQ,
            int correct,
            int wrong) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("keyValue", keyValue)
                .addValue("totalQ", totalQ)
                .addValue("correct", correct)
                .addValue("wrong", wrong);
    }

    private boolean isPostgreSql() {
        return dialectResolver.dialect() == DatabaseDialectResolver.DatabaseDialect.POSTGRESQL;
    }
}
