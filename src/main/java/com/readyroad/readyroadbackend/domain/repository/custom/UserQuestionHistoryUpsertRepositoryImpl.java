package com.readyroad.readyroadbackend.domain.repository.custom;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;

public class UserQuestionHistoryUpsertRepositoryImpl implements UserQuestionHistoryUpsertRepository {

    private static final String SHOWN_INSERT = """
            INSERT INTO user_question_history
                (user_id, question_id, question_ref_id, last_shown_at, last_shown_type,
                 times_shown, times_correct, times_wrong, created_at, updated_at)
            VALUES
                (:userId, :questionId, :questionId, :lastShownAt, :lastShownType,
                 1, 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;

    private static final String MYSQL_SHOWN_UPSERT = SHOWN_INSERT + """
            ON DUPLICATE KEY UPDATE
                last_shown_at = :lastShownAt,
                last_shown_type = :lastShownType,
                times_shown = times_shown + 1,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String POSTGRESQL_SHOWN_UPSERT = SHOWN_INSERT + """
            ON CONFLICT (user_id, question_ref_id) DO UPDATE SET
                last_shown_at = EXCLUDED.last_shown_at,
                last_shown_type = EXCLUDED.last_shown_type,
                times_shown = user_question_history.times_shown + 1,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String ANSWERED_INSERT = """
            INSERT INTO user_question_history
                (user_id, question_id, question_ref_id, answered_at, is_correct, last_answer_correct,
                 time_taken_seconds, last_shown_at, last_shown_type,
                 times_shown, times_correct, times_wrong, created_at, updated_at)
            VALUES
                (:userId, :questionId, :questionId, :answeredAt, :isCorrect, :isCorrect,
                 :timeTaken, :answeredAt, 'PRACTICE',
                 1, CASE WHEN :isCorrect THEN 1 ELSE 0 END,
                 CASE WHEN :isCorrect THEN 0 ELSE 1 END, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;

    private static final String MYSQL_ANSWERED_UPSERT = ANSWERED_INSERT + """
            ON DUPLICATE KEY UPDATE
                answered_at = :answeredAt,
                is_correct = :isCorrect,
                last_answer_correct = :isCorrect,
                time_taken_seconds = :timeTaken,
                last_shown_at = :answeredAt,
                last_shown_type = 'PRACTICE',
                times_shown = times_shown + 1,
                times_correct = times_correct + CASE WHEN :isCorrect THEN 1 ELSE 0 END,
                times_wrong = times_wrong + CASE WHEN :isCorrect THEN 0 ELSE 1 END,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String POSTGRESQL_ANSWERED_UPSERT = ANSWERED_INSERT + """
            ON CONFLICT (user_id, question_ref_id) DO UPDATE SET
                answered_at = EXCLUDED.answered_at,
                is_correct = EXCLUDED.is_correct,
                last_answer_correct = EXCLUDED.last_answer_correct,
                time_taken_seconds = EXCLUDED.time_taken_seconds,
                last_shown_at = EXCLUDED.last_shown_at,
                last_shown_type = EXCLUDED.last_shown_type,
                times_shown = user_question_history.times_shown + 1,
                times_correct = user_question_history.times_correct
                    + CASE WHEN EXCLUDED.is_correct THEN 1 ELSE 0 END,
                times_wrong = user_question_history.times_wrong
                    + CASE WHEN EXCLUDED.is_correct THEN 0 ELSE 1 END,
                updated_at = CURRENT_TIMESTAMP
            """;

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final DatabaseDialectResolver dialectResolver;

    public UserQuestionHistoryUpsertRepositoryImpl(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.dialectResolver = new DatabaseDialectResolver(dataSource);
    }

    @Override
    @Transactional
    public void upsertQuestionShown(
            Long userId,
            Long questionId,
            LocalDateTime lastShownAt,
            String lastShownType) {
        MapSqlParameterSource parameters = baseParameters(userId, questionId)
                .addValue("lastShownAt", lastShownAt)
                .addValue("lastShownType", lastShownType);
        jdbcTemplate.update(isPostgreSql() ? POSTGRESQL_SHOWN_UPSERT : MYSQL_SHOWN_UPSERT, parameters);
    }

    @Override
    @Transactional
    public void upsertQuestionAnswered(
            Long userId,
            Long questionId,
            LocalDateTime answeredAt,
            boolean isCorrect,
            int timeTaken) {
        MapSqlParameterSource parameters = baseParameters(userId, questionId)
                .addValue("answeredAt", answeredAt)
                .addValue("isCorrect", isCorrect)
                .addValue("timeTaken", timeTaken);
        jdbcTemplate.update(isPostgreSql() ? POSTGRESQL_ANSWERED_UPSERT : MYSQL_ANSWERED_UPSERT, parameters);
    }

    private MapSqlParameterSource baseParameters(Long userId, Long questionId) {
        return new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("questionId", questionId);
    }

    private boolean isPostgreSql() {
        return dialectResolver.dialect() == DatabaseDialectResolver.DatabaseDialect.POSTGRESQL;
    }
}
