package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.PracticeQuestion;
import com.readyroad.readyroadbackend.domain.repository.PracticeQuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PracticeQuestionService {

    private final PracticeQuestionRepository practiceQuestionRepository;

    public PracticeQuestionService(PracticeQuestionRepository practiceQuestionRepository) {
        this.practiceQuestionRepository = practiceQuestionRepository;
    }

    public List<PracticeQuestion> getQuestionsByLesson(Long lessonId) {
        return practiceQuestionRepository.findByLessonIdOrderByDisplayOrderAsc(lessonId);
    }

    public Optional<PracticeQuestion> getQuestionById(Long id) {
        return practiceQuestionRepository.findById(id);
    }

    public Long getQuestionsCountByLesson(Long lessonId) {
        return practiceQuestionRepository.countByLessonId(lessonId);
    }
}
