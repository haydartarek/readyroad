package com.readyroad.readyroadbackend.domain.repository.custom;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Transactional(readOnly = true)
public class QuizQuestionRandomRepositoryImpl implements QuizQuestionRandomRepository {

    private static final String PUBLISHED_FILTER = "q.is_active = true AND q.status = 'PUBLISHED'";

    @PersistenceContext
    private EntityManager entityManager;

    private final DatabaseDialectResolver dialectResolver;

    public QuizQuestionRandomRepositoryImpl(DataSource dataSource) {
        this.dialectResolver = new DatabaseDialectResolver(dataSource);
    }

    @Override
    public List<QuizQuestion> findRandomQuestions() {
        return questionResults(questionQuery(PUBLISHED_FILTER));
    }

    @Override
    public List<Long> findRandomQuestionIds(int limit) {
        return idQuery(PUBLISHED_FILTER, limit);
    }

    @Override
    public List<QuizQuestion> findRandomQuestionsByCategory(Long categoryId) {
        Query query = questionQuery("q.category_id = :categoryId AND " + PUBLISHED_FILTER);
        query.setParameter("categoryId", categoryId);
        return questionResults(query);
    }

    @Override
    public List<Long> findRandomQuestionIdsByCategory(Long categoryId, int limit) {
        Query query = randomQuery("SELECT q.id FROM quiz_questions q WHERE q.category_id = :categoryId AND "
                + PUBLISHED_FILTER);
        query.setParameter("categoryId", categoryId);
        return limitedIds(query, limit);
    }

    @Override
    public List<QuizQuestion> findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel difficulty) {
        Query query = questionQuery("q.difficulty_level = :difficulty AND " + PUBLISHED_FILTER);
        query.setParameter("difficulty", difficulty.name());
        return questionResults(query);
    }

    @Override
    public List<QuizQuestion> findCooldownEligibleQuestionsByDifficulty(
            Long userId,
            QuizQuestion.DifficultyLevel difficulty,
            LocalDateTime cooldownCutoff) {
        Query query = cooldownEligibleTheoryQuery("AND q.difficulty_level = :difficulty");
        query.setParameter("userId", userId);
        query.setParameter("difficulty", difficulty.name());
        query.setParameter("cooldownCutoff", cooldownCutoff);
        return fetchQuestionsInOrder(idResults(query));
    }

    @Override
    public List<QuizQuestion> findTheoryQuestionBankCandidates(String languageCode) {
        LocaleColumns locale = localeColumns(languageCode);
        String sql = """
                SELECT q.id
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
                GROUP BY q.id
                HAVING COUNT(o.id) BETWEEN 2 AND 3
                   AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1
                   AND SUM(CASE WHEN %s THEN 0 ELSE 1 END) = 0
                ORDER BY q.id
                """.formatted(
                usableText("q." + locale.questionColumn()),
                usableText("o." + locale.optionColumn()));
        return fetchQuestionsInOrder(idResults(entityManager.createNativeQuery(sql)));
    }

    @Override
    public List<QuizQuestion> findCooldownEligibleTheoryQuestions(
            Long userId,
            String languageCode,
            LocalDateTime cooldownCutoff) {
        Query query = cooldownEligibleTheoryQuery("", localeColumns(languageCode));
        query.setParameter("userId", userId);
        query.setParameter("cooldownCutoff", cooldownCutoff);
        return fetchQuestionsInOrder(idResults(query));
    }

    @Override
    public List<QuizQuestion> findRankedTheoryQuestionsForUser(
            Long userId,
            String languageCode) {
        Query query = rankedTheoryQuery(localeColumns(languageCode));
        query.setParameter("userId", userId);
        return fetchQuestionsInOrder(idResults(query));
    }

    @Override
    public List<Long> findRandomQuestionIdsByDifficulty(String difficulty, int limit) {
        Query query = randomQuery("SELECT q.id FROM quiz_questions q WHERE q.difficulty_level = :difficulty AND "
                + PUBLISHED_FILTER);
        query.setParameter("difficulty", difficulty);
        return limitedIds(query, limit);
    }

    private Query questionQuery(String filter) {
        String sql = "SELECT q.* FROM quiz_questions q WHERE " + filter;
        return randomEntityQuery(sql, QuizQuestion.class);
    }

    private List<Long> idQuery(String filter, int limit) {
        return limitedIds(randomQuery("SELECT q.id FROM quiz_questions q WHERE " + filter), limit);
    }

    private Query randomQuery(String sql) {
        String randomFunction = dialectResolver.dialect().randomFunction();
        return entityManager.createNativeQuery(sql + " ORDER BY " + randomFunction + "()");
    }

    private Query randomEntityQuery(String sql, Class<?> entityType) {
        String randomFunction = dialectResolver.dialect().randomFunction();
        return entityManager.createNativeQuery(sql + " ORDER BY " + randomFunction + "()", entityType);
    }

    private Query rankedTheoryQuery(LocaleColumns locale) {
        String randomFunction = dialectResolver.dialect().randomFunction();
        String sql = """
                SELECT q.id
                FROM quiz_questions q
                JOIN categories c ON c.id = q.category_id
                JOIN quiz_answer_options o
                  ON o.question_id = q.id
                 AND o.is_active = true
                LEFT JOIN user_question_history h
                  ON h.user_id = :userId
                 AND h.question_ref_id = q.id
                 AND h.question_type = 'THEORY'
                WHERE q.is_active = true
                  AND q.status = 'PUBLISHED'
                  AND c.is_active = true
                  AND c.content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                  AND %s
                GROUP BY q.id, h.last_presented_at
                HAVING COUNT(o.id) BETWEEN 2 AND 3
                   AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1
                   AND SUM(CASE WHEN %s THEN 0 ELSE 1 END) = 0
                ORDER BY CASE WHEN h.last_presented_at IS NULL THEN 0 ELSE 1 END,
                         h.last_presented_at ASC,
                """ + randomFunction + "()";
        sql = sql.formatted(
                usableText("q." + locale.questionColumn()),
                usableText("o." + locale.optionColumn()));
        return entityManager.createNativeQuery(sql);
    }
    private Query cooldownEligibleTheoryQuery(String additionalFilter) {
        return cooldownEligibleTheoryQuery(additionalFilter, localeColumns("en"));
    }

    private Query cooldownEligibleTheoryQuery(String additionalFilter, LocaleColumns locale) {
        String randomFunction = dialectResolver.dialect().randomFunction();
        String sql = """
                SELECT q.id
                FROM quiz_questions q
                JOIN categories c ON c.id = q.category_id
                JOIN quiz_answer_options o
                  ON o.question_id = q.id
                 AND o.is_active = true
                LEFT JOIN user_question_history h
                  ON h.user_id = :userId
                 AND h.question_ref_id = q.id
                 AND h.question_type = 'THEORY'
                WHERE q.is_active = true
                  AND q.status = 'PUBLISHED'
                  AND c.is_active = true
                  AND c.content_scope IN ('THEORETICAL_EXAM', 'BOTH')
                  AND %s
                  AND (h.last_presented_at IS NULL OR h.last_presented_at <= :cooldownCutoff)
                """ + additionalFilter + """

                GROUP BY q.id, h.last_presented_at
                HAVING COUNT(o.id) BETWEEN 2 AND 3
                   AND SUM(CASE WHEN o.is_correct = true THEN 1 ELSE 0 END) = 1
                   AND SUM(CASE WHEN %s THEN 0 ELSE 1 END) = 0
                ORDER BY CASE WHEN h.last_presented_at IS NULL THEN 0 ELSE 1 END,
                         h.last_presented_at ASC,
                """ + randomFunction + "()";
        sql = sql.formatted(
                usableText("q." + locale.questionColumn()),
                usableText("o." + locale.optionColumn()));
        return entityManager.createNativeQuery(sql);
    }

    private List<QuizQuestion> fetchQuestionsInOrder(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        List<QuizQuestion> fetched = entityManager.createQuery("""
                        SELECT DISTINCT q
                        FROM QuizQuestion q
                        JOIN FETCH q.category
                        LEFT JOIN FETCH q.options
                        WHERE q.id IN :ids
                        """, QuizQuestion.class)
                .setParameter("ids", ids)
                .getResultList();
        Map<Long, QuizQuestion> byId = new LinkedHashMap<>();
        fetched.forEach(question -> byId.put(question.getId(), question));
        return ids.stream().map(byId::get).filter(java.util.Objects::nonNull).toList();
    }

    private List<Long> idResults(Query query) {
        List<?> results = query.getResultList();
        return results.stream()
                .map(result -> ((Number) result).longValue())
                .toList();
    }

    private static LocaleColumns localeColumns(String languageCode) {
        return switch (languageCode == null ? "" : languageCode.trim().toLowerCase(Locale.ROOT)) {
            case "ar" -> new LocaleColumns("question_ar", "option_text_ar");
            case "nl" -> new LocaleColumns("question_nl", "option_text_nl");
            case "fr" -> new LocaleColumns("question_fr", "option_text_fr");
            case "en" -> new LocaleColumns("question_en", "option_text_en");
            default -> throw new IllegalArgumentException("Unsupported theory exam language");
        };
    }

    private static String usableText(String column) {
        return column + " IS NOT NULL"
                + " AND TRIM(" + column + ") <> ''"
                + " AND LOWER(TRIM(" + column + ")) NOT IN"
                + " ('option a', 'option b', 'option c', 'optie a', 'optie b', 'optie c')"
                + " AND POSITION('??' IN " + column + ") = 0";
    }

    private record LocaleColumns(String questionColumn, String optionColumn) {
    }

    @SuppressWarnings("unchecked")
    private List<QuizQuestion> questionResults(Query query) {
        return query.getResultList();
    }

    private List<Long> limitedIds(Query query, int limit) {
        if (limit <= 0) {
            return List.of();
        }
        query.setMaxResults(limit);
        List<?> results = query.getResultList();
        return results.stream()
                .map(Number.class::cast)
                .map(Number::longValue)
                .toList();
    }
}
