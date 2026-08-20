package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Category;
import com.readyroad.readyroadbackend.domain.entity.ExamSimulationQuestion;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.CategorySnapshot;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.LocalizedText;
import com.readyroad.readyroadbackend.dto.exam.TheoryExamQuestionSnapshot.OptionSnapshot;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TheoryExamQuestionSnapshotService {

    static final short CURRENT_VERSION = 1;

    private final ObjectMapper objectMapper;
    private final RoadSignReferenceTextResolver textResolver;

    public TheoryExamQuestionSnapshotService(
            ObjectMapper objectMapper,
            RoadSignReferenceTextResolver textResolver) {
        this.objectMapper = objectMapper;
        this.textResolver = textResolver;
    }

    public void captureInto(ExamSimulationQuestion examQuestion, QuizQuestion question) {
        TheoryExamQuestionSnapshot snapshot = new TheoryExamQuestionSnapshot(
                CURRENT_VERSION,
                question.getId(),
                localized(
                        question.getQuestionEn(),
                        question.getQuestionNl(),
                        question.getQuestionFr(),
                        question.getQuestionAr()),
                localized(
                        question.getExplanationEn(),
                        question.getExplanationNl(),
                        question.getExplanationFr(),
                        question.getExplanationAr()),
                question.getContentImageUrl(),
                category(question.getCategory()),
                question.getDifficultyLevel() == null ? null : question.getDifficultyLevel().name(),
                options(question.getDeliverableOptions()));
        examQuestion.setHistoricalSnapshotVersion(CURRENT_VERSION);
        examQuestion.setHistoricalSnapshotJson(write(snapshot));
    }

    public TheoryExamQuestionSnapshot read(ExamSimulationQuestion examQuestion) {
        Short version = examQuestion.getHistoricalSnapshotVersion();
        String payload = examQuestion.getHistoricalSnapshotJson();
        if (version == null && (payload == null || payload.isBlank())) {
            return null;
        }
        if (version == null || version != CURRENT_VERSION || payload == null || payload.isBlank()) {
            throw new IllegalStateException("Unsupported or incomplete theory exam historical snapshot");
        }
        try {
            TheoryExamQuestionSnapshot snapshot = objectMapper.readValue(payload, TheoryExamQuestionSnapshot.class);
            if (snapshot.version() != CURRENT_VERSION || !examQuestion.getQuestionId().equals(snapshot.questionId())) {
                throw new IllegalStateException("Theory exam historical snapshot identity mismatch");
            }
            return snapshot;
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to read theory exam historical snapshot", exception);
        }
    }

    private CategorySnapshot category(Category category) {
        if (category == null) {
            return null;
        }
        return new CategorySnapshot(
                category.getId(),
                category.getCode(),
                localized(category.getNameEn(), category.getNameNl(), category.getNameFr(), category.getNameAr()));
    }

    private List<OptionSnapshot> options(List<QuizAnswerOption> options) {
        return options.stream()
                .map(option -> new OptionSnapshot(
                        option.getId(),
                        localized(
                                option.getOptionTextEn(),
                                option.getOptionTextNl(),
                                option.getOptionTextFr(),
                                option.getOptionTextAr()),
                        Boolean.TRUE.equals(option.getIsCorrect()),
                        option.getDisplayOrder()))
                .toList();
    }

    private LocalizedText localized(String en, String nl, String fr, String ar) {
        return new LocalizedText(
                textResolver.resolveEn(en),
                textResolver.resolveNl(nl),
                textResolver.resolveFr(fr),
                textResolver.resolveAr(ar));
    }

    private String write(TheoryExamQuestionSnapshot snapshot) {
        try {
            return objectMapper.writeValueAsString(snapshot);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to create theory exam historical snapshot", exception);
        }
    }
}
