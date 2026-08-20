package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.TheoryTimeoutAnalysisResponse;
import com.readyroad.readyroadbackend.dto.TheoryTimeoutAnalysisResponse.TheoryTimeoutItem;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TheoryTimeoutAnalysisService {

    private final ExamSimulationAnswerRepository answerRepository;
    private final ExamSimulationQuestionRepository examQuestionRepository;
    private final TheoryExamQuestionSnapshotService snapshotService;
    private final RoadSignReferenceTextResolver textResolver;

    public TheoryTimeoutAnalysisService(
            ExamSimulationAnswerRepository answerRepository,
            ExamSimulationQuestionRepository examQuestionRepository,
            TheoryExamQuestionSnapshotService snapshotService,
            RoadSignReferenceTextResolver textResolver) {
        this.answerRepository = answerRepository;
        this.examQuestionRepository = examQuestionRepository;
        this.snapshotService = snapshotService;
        this.textResolver = textResolver;
    }

    @Transactional(readOnly = true)
    public TheoryTimeoutAnalysisResponse getAnalysis(Long userId, int requestedLimit) {
        int limit = Math.max(1, Math.min(requestedLimit, 20));
        List<TheoryTimeoutItem> items = answerRepository
                .findRecentCompletedTheoryTimeouts(userId, PageRequest.of(0, limit))
                .stream()
                .map(this::map)
                .toList();
        return new TheoryTimeoutAnalysisResponse(
                answerRepository.countCompletedTheoryTimeouts(userId),
                items);
    }

    private TheoryTimeoutItem map(ExamSimulationAnswer answer) {
        QuizQuestion question = answer.getQuestion();
        Category category = question.getCategory();
        ExamSimulationQuestion examQuestion = examQuestionRepository
                .findByExamIdAndQuestionId(answer.getExam().getId(), question.getId())
                .orElse(null);
        TheoryExamQuestionSnapshot snapshot = examQuestion == null ? null : snapshotService.read(examQuestion);
        CategorySnapshot categorySnapshot = snapshot == null ? null : snapshot.category();

        return new TheoryTimeoutItem(
                answer.getExam().getId(),
                question.getId(),
                snapshot == null ? textResolver.resolveEn(question.getQuestionEn()) : en(snapshot.questionText()),
                snapshot == null ? textResolver.resolveNl(question.getQuestionNl()) : nl(snapshot.questionText()),
                snapshot == null ? textResolver.resolveFr(question.getQuestionFr()) : fr(snapshot.questionText()),
                snapshot == null ? textResolver.resolveAr(question.getQuestionAr()) : ar(snapshot.questionText()),
                categorySnapshot == null ? category == null ? null : category.getCode() : categorySnapshot.code(),
                categorySnapshot == null ? category == null ? null : category.getNameEn() : en(categorySnapshot.name()),
                categorySnapshot == null ? category == null ? null : category.getNameNl() : nl(categorySnapshot.name()),
                categorySnapshot == null ? category == null ? null : category.getNameFr() : fr(categorySnapshot.name()),
                categorySnapshot == null ? category == null ? null : category.getNameAr() : ar(categorySnapshot.name()),
                snapshot == null
                        ? question.getDifficultyLevel() == null ? null : question.getDifficultyLevel().name()
                        : snapshot.difficulty(),
                answer.getTimedOutAt(),
                "/exam/results/" + answer.getExam().getId());
    }

    private static String en(LocalizedText text) {
        return text == null ? null : text.en();
    }

    private static String nl(LocalizedText text) {
        return text == null ? null : text.nl();
    }

    private static String fr(LocalizedText text) {
        return text == null ? null : text.fr();
    }

    private static String ar(LocalizedText text) {
        return text == null ? null : text.ar();
    }
}
