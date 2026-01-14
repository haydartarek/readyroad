package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.ExamQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;

    public ExamQuestionService(ExamQuestionRepository examQuestionRepository) {
        this.examQuestionRepository = examQuestionRepository;
    }

    public List<ExamQuestion> getAllExamQuestions() {
        return examQuestionRepository.findByIsActiveTrue();
    }

    public Optional<ExamQuestion> getQuestionById(Long id) {
        return examQuestionRepository.findById(id);
    }

    public List<ExamQuestion> getRandomQuestions(int limit) {
        List<ExamQuestion> questions = examQuestionRepository.findRandomQuestions();
        return questions.stream()
                .limit(limit)
                .toList();
    }

    public List<ExamQuestion> getRandomQuestionsByCategory(Long categoryId, int limit) {
        List<ExamQuestion> questions = examQuestionRepository.findRandomQuestionsByCategory(categoryId);
        return questions.stream()
                .limit(limit)
                .toList();
    }

    public Long getTotalQuestionsCount() {
        return examQuestionRepository.countByIsActiveTrue();
    }

    public Long getQuestionsCountByCategory(Long categoryId) {
        return examQuestionRepository.countByCategoryIdAndIsActiveTrue(categoryId);
    }
}
