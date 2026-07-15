package com.readyroad.readyroadbackend.domain.repository.custom;

import com.readyroad.readyroadbackend.domain.entity.DevExamDifficulty;
import com.readyroad.readyroadbackend.domain.entity.DevExamQuestion;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DevExamQuestionRandomRepository {

    List<DevExamQuestion> findRandomByCategoryAndDifficulty(
            Long categoryId,
            DevExamDifficulty difficulty,
            Pageable pageable);
}
