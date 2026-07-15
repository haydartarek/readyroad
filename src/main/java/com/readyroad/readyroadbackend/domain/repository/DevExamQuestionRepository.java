package com.readyroad.readyroadbackend.domain.repository;

import com.readyroad.readyroadbackend.domain.entity.DevExamDifficulty;
import com.readyroad.readyroadbackend.domain.entity.DevExamQuestion;
import com.readyroad.readyroadbackend.domain.repository.custom.DevExamQuestionRandomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DevExamQuestionRepository extends JpaRepository<DevExamQuestion, Long>,
        DevExamQuestionRandomRepository {

    List<DevExamQuestion> findByCategory_IdAndDifficultyAndIsActiveTrue(
            Long categoryId, DevExamDifficulty difficulty);
}
