package com.readyroad.readyroadbackend.domain.repository.custom;

import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;

import java.util.List;

public interface QuizQuestionRandomRepository {

    List<QuizQuestion> findRandomQuestions();

    List<Long> findRandomQuestionIds(int limit);

    List<QuizQuestion> findRandomQuestionsByCategory(Long categoryId);

    List<Long> findRandomQuestionIdsByCategory(Long categoryId, int limit);

    List<QuizQuestion> findRandomQuestionsByDifficulty(QuizQuestion.DifficultyLevel difficulty);

    List<Long> findRandomQuestionIdsByDifficulty(String difficulty, int limit);
}
