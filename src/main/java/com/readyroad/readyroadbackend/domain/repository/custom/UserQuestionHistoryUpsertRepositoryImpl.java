package com.readyroad.readyroadbackend.domain.repository.custom;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;

public class UserQuestionHistoryUpsertRepositoryImpl implements UserQuestionHistoryUpsertRepository {

    private static final String THEORY_SOURCE = "THEORY";

    private static final String PRESENTED_INSERT = """
            INSERT INTO user_question_history
                (user_id, question_id, question_type, question_ref_id,
                 last_presented_at, times_presented, last_shown_at, last_shown_type,
                 times_shown, times_correct, times_wrong, created_at, updated_at)
            VALUES
                (:userId, :questionId, :questionType, :questionId,
                 :presentedAt, 1, :presentedAt, :presentationContext,
                 1, 0, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;

    private static final String MYSQL_PRESENTED_UPSERT = PRESENTED_INSERT + """
            ON DUPLICATE KEY UPDATE
                question_type = :questionType,
                last_presented_at = :presentedAt,
                times_presented = times_presented + 1,
                last_shown_at = :presentedAt,
                last_shown_type = :presentationContext,
                times_shown = times_shown + 1,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String POSTGRESQL_PRESENTED_UPSERT = PRESENTED_INSERT + """
            ON CONFLICT (user_id, question_ref_id) DO UPDATE SET
                question_type = EXCLUDED.question_type,
                last_presented_at = EXCLUDED.last_presented_at,
                times_presented = user_question_history.times_presented + 1,
                last_shown_at = EXCLUDED.last_shown_at,
                last_shown_type = EXCLUDED.last_shown_type,
                times_shown = user_question_history.times_shown + 1,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String ANSWERED_INSERT = """
            INSERT INTO user_question_history
                (user_id, question_id, question_type, question_ref_id,
                 answered_at, is_correct, last_answer_correct,
                 time_taken_seconds, last_presented_at, times_presented,
                 last_shown_at, last_shown_type,
                 times_shown, times_correct, times_wrong, created_at, updated_at)
            VALUES
                (:userId, :questionId, :questionType, :questionId,
                 :answeredAt, :isCorrect, :isCorrect,
                 :timeTaken, NULL, 0, NULL, NULL,
                 0, CASE WHEN :isCorrect THEN 1 ELSE 0 END,
                 CASE WHEN :isCorrect THEN 0 ELSE 1 END, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """;

    private static final String MYSQL_ANSWERED_UPSERT = ANSWERED_INSERT + """
            ON DUPLICATE KEY UPDATE
                question_type = :questionType,
                answered_at = :answeredAt,
                is_correct = :isCorrect,
                last_answer_correct = :isCorrect,
                time_taken_seconds = :timeTaken,
                times_correct = times_correct + CASE WHEN :isCorrect THEN 1 ELSE 0 END,
                times_wrong = times_wrong + CASE WHEN :isCorrect THEN 0 ELSE 1 END,
                updated_at = CURRENT_TIMESTAMP
            """;

    private static final String POSTGRESQL_ANSWERED_UPSERT = ANSWERED_INSERT + """
            ON CONFLICT (user_id, question_ref_id) DO UPDATE SET
                question_type = EXCLUDED.question_type,
                answered_at = EXCLUDED.answered_at,
                is_correct = EXCLUDED.is_correct,
                last_answer_correct = EXCLUDED.last_answer_correct,
                time_taken_seconds = EXCLUDED.time_taken_seconds,
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
    public void upsertQuestionPresented(
            Long userId,
            Long questionId,
            LocalDateTime presentedAt,
            String presentationContext) {
        MapSqlParameterSource parameters = baseParameters(userId, questionId)
                .addValue("presentedAt", presentedAt)
                .addValue("presentationContext", presentationContext);
        jdbcTemplate.update(
                isPostgreSql() ? POSTGRESQL_PRESENTED_UPSERT : MYSQL_PRESENTED_UPSERT,
                parameters);
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
                .addValue("questionId", questionId)
                .addValue("questionType", THEORY_SOURCE);
    }

    private boolean isPostgreSql() {
        return dialectResolver.dialect() == DatabaseDialectResolver.DatabaseDialect.POSTGRESQL;
    }
}
