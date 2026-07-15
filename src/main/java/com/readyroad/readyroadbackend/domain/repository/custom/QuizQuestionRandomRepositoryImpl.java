package com.readyroad.readyroadbackend.domain.repository.custom;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

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
