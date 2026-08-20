package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.ExamSimulationAnswer;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationAnswerRepository;
import com.readyroad.readyroadbackend.domain.repository.ExamSimulationQuestionRepository;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.HistoricalTheoryQuestion;
import com.readyroad.readyroadbackend.dto.admin.AdminLearningDtos.TheoryExamHistoryResult;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.OptionSnapshot;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class AdminTheoryExamHistoryService {

    public static final String SNAPSHOT_COMPLETE = "SNAPSHOT_COMPLETE";
    public static final String SNAPSHOT_PARTIAL = "SNAPSHOT_PARTIAL";
    public static final String LEGACY_NO_SNAPSHOT = "LEGACY_NO_SNAPSHOT";

    private final ExamSimulationQuestionRepository questionRepository;
    private final ExamSimulationAnswerRepository answerRepository;
    private final TheoryExamQuestionSnapshotService snapshotService;

    public AdminTheoryExamHistoryService(
            ExamSimulationQuestionRepository questionRepository,
            ExamSimulationAnswerRepository answerRepository,
            TheoryExamQuestionSnapshotService snapshotService) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.snapshotService = snapshotService;
    }

    public HistoricalResult load(long examId) {
        List<ExamSimulationQuestion> examQuestions = questionRepository.findByExamIdOrderByQuestionOrder(examId);
        Map<Long, ExamSimulationAnswer> answers = answerRepository.findByExamId(examId).stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), Function.identity()));

        int snapshots = 0;
        List<HistoricalTheoryQuestion> questions = new java.util.ArrayList<>(examQuestions.size());
        for (ExamSimulationQuestion examQuestion : examQuestions) {
            TheoryExamQuestionSnapshot snapshot = snapshotService.read(examQuestion);
            if (snapshot != null) {
                snapshots++;
            }
            questions.add(map(examQuestion, answers.get(examQuestion.getQuestionId()), snapshot));
        }

        String status = snapshots == 0
                ? LEGACY_NO_SNAPSHOT
                : snapshots == examQuestions.size() ? SNAPSHOT_COMPLETE : SNAPSHOT_PARTIAL;
        return new HistoricalResult(status, new TheoryExamHistoryResult(List.copyOf(questions)));
    }

    private static HistoricalTheoryQuestion map(
            ExamSimulationQuestion examQuestion,
            ExamSimulationAnswer answer,
            TheoryExamQuestionSnapshot snapshot) {
        if (snapshot == null) {
            return legacy(examQuestion, answer);
        }

        Long selectedId = answer == null || answer.getSelectedOption() == null
                ? null : answer.getSelectedOption().getId();
        Long correctId = answer == null || answer.getCorrectOption() == null
                ? snapshot.options().stream().filter(OptionSnapshot::correct).map(OptionSnapshot::id).findFirst().orElse(null)
                : answer.getCorrectOption().getId();
        OptionSnapshot selected = option(snapshot, selectedId);
        OptionSnapshot correct = option(snapshot, correctId);
        CategorySnapshot category = snapshot.category();

        return new HistoricalTheoryQuestion(
                snapshot.questionId(),
                examQuestion.getQuestionOrder(),
                en(snapshot.questionText()), nl(snapshot.questionText()), fr(snapshot.questionText()), ar(snapshot.questionText()),
                selectedId,
                en(text(selected)), nl(text(selected)), fr(text(selected)), ar(text(selected)),
                correctId,
                en(text(correct)), nl(text(correct)), fr(text(correct)), ar(text(correct)),
                en(snapshot.explanation()), nl(snapshot.explanation()), fr(snapshot.explanation()), ar(snapshot.explanation()),
                category == null ? null : category.code(),
                category == null ? null : en(category.name()),
                category == null ? null : nl(category.name()),
                category == null ? null : fr(category.name()),
                category == null ? null : ar(category.name()),
                snapshot.difficulty(),
                snapshot.contentImageUrl(),
                answer == null || answer.isTimedOut() ? null : answer.getIsCorrect(),
                answer != null && !answer.isTimedOut(),
                true);
    }

    private static HistoricalTheoryQuestion legacy(
            ExamSimulationQuestion examQuestion,
            ExamSimulationAnswer answer) {
        Long selectedId = answer == null || answer.getSelectedOption() == null
                ? null : answer.getSelectedOption().getId();
        Long correctId = answer == null || answer.getCorrectOption() == null
                ? null : answer.getCorrectOption().getId();
        return new HistoricalTheoryQuestion(
                examQuestion.getQuestionId(), examQuestion.getQuestionOrder(),
                null, null, null, null,
                selectedId, null, null, null, null,
                correctId, null, null, null, null,
                null, null, null, null,
                null, null, null, null, null,
                null, null,
                answer == null || answer.isTimedOut() ? null : answer.getIsCorrect(),
                answer != null && !answer.isTimedOut(),
                false);
    }

    private static OptionSnapshot option(TheoryExamQuestionSnapshot snapshot, Long id) {
        if (id == null) {
            return null;
        }
        return snapshot.options().stream().filter(option -> id.equals(option.id())).findFirst().orElse(null);
    }

    private static LocalizedText text(OptionSnapshot option) {
        return option == null ? null : option.text();
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

    public record HistoricalResult(
            String status,
            TheoryExamHistoryResult result) {
    }
}
