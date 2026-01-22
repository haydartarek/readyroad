package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Quiz Answer Option Repository
 *
 * **Phase 2 Restoration:** Created January 18, 2026
 * Provides data access for quiz answer options
 */
@Repository
public interface QuizAnswerOptionRepository extends JpaRepository<QuizAnswerOption, Long> {

    /**
     * Find all options for a specific question
     */
    List<QuizAnswerOption> findByQuestionIdOrderByDisplayOrder(Long questionId);

    /**
     * Find the correct option for a question
     */
    QuizAnswerOption findByQuestionIdAndIsCorrectTrue(Long questionId);

    /**
     * Count options for a question
     */
    Long countByQuestionId(Long questionId);
}
