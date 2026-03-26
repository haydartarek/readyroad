package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamDifficulty;
import com.readyroad.readyroadbackend.domain.entity.DevExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevExamQuestionRepository extends JpaRepository<DevExamQuestion, Long> {

    @Query("SELECT q FROM DevExamQuestion q WHERE q.category.id = :categoryId " +
            "AND q.difficulty = :difficulty AND q.isActive = true ORDER BY RAND()")
    List<DevExamQuestion> findRandomByCategoryAndDifficulty(
            @Param("categoryId") Long categoryId,
            @Param("difficulty") DevExamDifficulty difficulty,
            org.springframework.data.domain.Pageable pageable);

    List<DevExamQuestion> findByCategory_IdAndDifficultyAndIsActiveTrue(
            Long categoryId, DevExamDifficulty difficulty);
}
