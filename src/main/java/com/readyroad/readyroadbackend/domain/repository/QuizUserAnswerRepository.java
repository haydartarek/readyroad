package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.QuizUserAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuizUserAnswerRepository extends JpaRepository<QuizUserAnswer, Long> {

    /**
     * Check if any answers reference a given question (by polymorphic
     * questionRefId).
     */
    boolean existsByQuestionRefId(Long questionRefId);
}
