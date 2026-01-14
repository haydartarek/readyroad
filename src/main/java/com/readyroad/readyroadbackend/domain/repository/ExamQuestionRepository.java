package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    List<ExamQuestion> findByCategoryIdAndIsActiveTrue(Long categoryId);
    List<ExamQuestion> findByIsActiveTrue();
    
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.isActive = true ORDER BY RAND()")
    List<ExamQuestion> findRandomQuestions();
    
    @Query("SELECT eq FROM ExamQuestion eq WHERE eq.category.id = :categoryId AND eq.isActive = true ORDER BY RAND()")
    List<ExamQuestion> findRandomQuestionsByCategory(Long categoryId);
    
    Long countByIsActiveTrue();
    Long countByCategoryIdAndIsActiveTrue(Long categoryId);
}
