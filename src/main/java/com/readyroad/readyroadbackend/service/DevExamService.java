package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.*;
import com.readyroad.readyroadbackend.domain.repository.*;
import com.readyroad.readyroadbackend.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DevExamService {

    private final DevExamCategoryRepository categoryRepo;
    private final DevExamCategoryI18nRepository i18nRepo;
    private final DevExamQuestionRepository questionRepo;
    private final DevExamChoiceRepository choiceRepo;
    private final DevExamSettingRepository settingRepo;

    // ─── Categories ─────────────────────────────────────────────────────────

    public List<DevExamCategoryDto> getCategories(String lang) {
        return categoryRepo.findByIsActiveTrue().stream()
                .map(cat -> toCategoryDto(cat, lang))
                .collect(Collectors.toList());
    }

    public DevExamCategoryDto getCategoryBySlug(String slug, String lang) {
        DevExamCategory cat = categoryRepo.findActiveBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + slug));
        return toCategoryDto(cat, lang);
    }

    // ─── Questions ──────────────────────────────────────────────────────────

    public List<DevExamQuestionDto> getQuestions(String slug, String level, String lang, int limit) {
        DevExamCategory cat = categoryRepo.findActiveBySlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Category not found: " + slug));

        DevExamDifficulty difficulty;
        try {
            difficulty = DevExamDifficulty.valueOf(level.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid difficulty level: " + level);
        }

        List<DevExamQuestion> questions = questionRepo.findRandomByCategoryAndDifficulty(
                cat.getId(), difficulty, PageRequest.of(0, limit));

        return questions.stream()
                .map(q -> toQuestionDto(q, lang))
                .collect(Collectors.toList());
    }

    public DevExamAnswerCheckDto checkAnswer(Long questionId, Long choiceId) {
        DevExamQuestion question = questionRepo.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));

        if (!Boolean.TRUE.equals(question.getIsActive())) {
            throw new IllegalArgumentException("Question is inactive: " + questionId);
        }

        choiceRepo.findByIdAndQuestion_Id(choiceId, questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Choice " + choiceId + " does not belong to question " + questionId));

        Long correctChoiceId = choiceRepo.findByQuestion_IdOrderBySortOrder(questionId).stream()
                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                .map(DevExamChoice::getId)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No correct choice configured for question " + questionId));

        DevExamAnswerCheckDto dto = new DevExamAnswerCheckDto();
        dto.setCorrectChoiceId(correctChoiceId);
        dto.setCorrect(correctChoiceId.equals(choiceId));
        return dto;
    }

    // ─── Mapping helpers ────────────────────────────────────────────────────

    private DevExamCategoryDto toCategoryDto(DevExamCategory cat, String lang) {
        DevExamCategoryDto dto = new DevExamCategoryDto();
        dto.setId(cat.getId());
        dto.setSlug(cat.getSlug());

        // Resolve i18n name / description
        i18nRepo.findByCategory_IdAndLang(cat.getId(), lang).ifPresentOrElse(
                i18n -> {
                    dto.setName(i18n.getName());
                    dto.setDescription(i18n.getDescription());
                },
                () -> i18nRepo.findByCategory_IdAndLang(cat.getId(), "en").ifPresentOrElse(
                        fallback -> {
                            dto.setName(fallback.getName());
                            dto.setDescription(fallback.getDescription());
                        },
                        () -> {
                            dto.setName(cat.getSlug());
                            dto.setDescription(null);
                        }));

        settingRepo.findByCategory_Id(cat.getId()).ifPresent(s -> {
            dto.setTimeLimitMinutes(s.getTimeLimitMinutes());
            dto.setPassingScorePercent(s.getPassingScorePercent());
        });

        dto.setDifficulties(List.of("BEGINNER", "INTERMEDIATE", "ADVANCED"));
        return dto;
    }

    private DevExamQuestionDto toQuestionDto(DevExamQuestion q, String lang) {
        DevExamQuestionDto dto = new DevExamQuestionDto();
        dto.setId(q.getId());
        dto.setDifficulty(q.getDifficulty().name());
        dto.setQuestion(resolveText(q.getQuestionEn(), q.getQuestionAr(), q.getQuestionNl(), q.getQuestionFr(), lang));
        dto.setExplanation(resolveText(q.getExplanationEn(), q.getExplanationAr(), q.getExplanationNl(),
                q.getExplanationFr(), lang));

        List<DevExamChoice> publicChoices = buildPublicChoices(q.getId());
        List<DevExamChoiceDto> choices = publicChoices.stream()
                .map(c -> toChoiceDto(c, lang))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(choices);
        for (int i = 0; i < choices.size(); i++) {
            choices.get(i).setSortOrder(i + 1);
        }
        dto.setChoices(choices);
        return dto;
    }

    private DevExamChoiceDto toChoiceDto(DevExamChoice c, String lang) {
        DevExamChoiceDto dto = new DevExamChoiceDto();
        dto.setId(c.getId());
        dto.setSortOrder(c.getSortOrder());
        dto.setText(resolveText(c.getTextEn(), c.getTextAr(), c.getTextNl(), c.getTextFr(), lang));
        return dto;
    }

    private List<DevExamChoice> buildPublicChoices(Long questionId) {
        List<DevExamChoice> allChoices = new ArrayList<>(choiceRepo.findByQuestion_IdOrderBySortOrder(questionId));
        List<DevExamChoice> correctChoices = allChoices.stream()
                .filter(choice -> Boolean.TRUE.equals(choice.getIsCorrect()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (correctChoices.size() != 1) {
            throw new IllegalStateException("Expected exactly one correct choice for assessment question " + questionId);
        }

        if (allChoices.size() <= 3) {
            return allChoices;
        }

        List<DevExamChoice> incorrectChoices = allChoices.stream()
                .filter(choice -> !Boolean.TRUE.equals(choice.getIsCorrect()))
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(incorrectChoices);

        List<DevExamChoice> publicChoices = new ArrayList<>();
        publicChoices.add(correctChoices.get(0));
        publicChoices.addAll(incorrectChoices.stream().limit(2).toList());
        return publicChoices;
    }

    /** Fall back to EN when the requested language field is null. */
    private String resolveText(String en, String ar, String nl, String fr, String lang) {
        String value = switch (lang) {
            case "ar" -> ar;
            case "nl" -> nl;
            case "fr" -> fr;
            default -> en;
        };
        return (value != null && !value.isBlank()) ? value : en;
    }
}
