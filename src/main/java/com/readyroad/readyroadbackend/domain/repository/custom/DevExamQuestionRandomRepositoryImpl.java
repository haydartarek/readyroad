package com.readyroad.readyroadbackend.domain.repository.custom;

import com.readyroad.readyroadbackend.domain.entity.DevExamDifficulty;
import com.readyroad.readyroadbackend.domain.entity.DevExamQuestion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

@Transactional(readOnly = true)
public class DevExamQuestionRandomRepositoryImpl implements DevExamQuestionRandomRepository {

    @PersistenceContext
    private EntityManager entityManager;

    private final DatabaseDialectResolver dialectResolver;

    public DevExamQuestionRandomRepositoryImpl(DataSource dataSource) {
        this.dialectResolver = new DatabaseDialectResolver(dataSource);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DevExamQuestion> findRandomByCategoryAndDifficulty(
            Long categoryId,
            DevExamDifficulty difficulty,
            Pageable pageable) {
        String randomFunction = dialectResolver.dialect().randomFunction();
        Query query = entityManager.createNativeQuery(
                "SELECT q.* FROM dev_exam_questions q "
                        + "WHERE q.category_id = :categoryId "
                        + "AND q.difficulty = :difficulty "
                        + "AND q.is_active = true "
                        + "ORDER BY " + randomFunction + "()",
                DevExamQuestion.class);
        query.setParameter("categoryId", categoryId);
        query.setParameter("difficulty", difficulty.name());
        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult(Math.toIntExact(pageable.getOffset()));
            query.setMaxResults(pageable.getPageSize());
        }
        return query.getResultList();
    }
}
