package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizUserAnswerRepository extends JpaRepository<QuizUserAnswer, Long> {

    /**
     * Check if any answers reference a given question (by polymorphic
     * questionRefId).
     */
    boolean existsByQuestionRefId(Long questionRefId);

    /**
     * Check if any NON-test answers reference a given question.
     * Returns true if real (non-test) references exist.
     */
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END " +
            "FROM QuizUserAnswer a WHERE a.questionRefId = :qid AND a.isTestData = false")
    boolean existsRealReferencesByQuestionRefId(@Param("qid") Long questionRefId);

    /**
     * Count test-data answers only.
     */
    long countByIsTestDataTrue();

    /**
     * Delete all test-data answers.
     */
    @Modifying
    @Query("DELETE FROM QuizUserAnswer a WHERE a.isTestData = true")
    int deleteAllTestData();
}
