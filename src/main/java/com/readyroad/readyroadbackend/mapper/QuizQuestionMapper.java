package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizAnswerOptionDTO;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.service.RoadSignReferenceTextResolver;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════════
 * QuizQuestionMapper
 * ═══════════════════════════════════════════════════════════════
 *
 * Purpose: Convert QuizQuestion Entity → QuizQuestionDTO
 *
 * Architecture:
 * - Pure Mapper (no business logic)
 * - Content-agnostic (works for Traffic, Math, Medical, etc.)
 * - Complies with Law #5: Deliberate Ignorance
 *
 * Used by:
 * - QuizController delivery flows
 * - Tests (ContentSwapProofTest)
 *
 * @see com.readyroad.readyroadbackend.dto.QuizQuestionDTO
 * @see com.readyroad.readyroadbackend.domain.entity.QuizQuestion
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class QuizQuestionMapper {

    private final RoadSignReferenceTextResolver roadSignReferenceTextResolver;

    /**
     * Convert QuizQuestion entity to DTO
     *
     * @param question the question entity (may be from any content domain)
     * @return QuizQuestionDTO with all generic fields mapped
     */
    public QuizQuestionDTO toDTO(QuizQuestion question) {
        if (question == null) {
            return null;
        }

        QuizQuestionDTO dto = new QuizQuestionDTO();

        // Basic fields
        dto.setId(question.getId());
        dto.setQuestionAr(roadSignReferenceTextResolver.resolveAr(question.getQuestionAr()));
        dto.setQuestionEn(roadSignReferenceTextResolver.resolveEn(question.getQuestionEn()));
        dto.setQuestionNl(roadSignReferenceTextResolver.resolveNl(question.getQuestionNl()));
        dto.setQuestionFr(roadSignReferenceTextResolver.resolveFr(question.getQuestionFr()));

        // Type and difficulty
        dto.setQuestionType(question.getQuestionType());
        dto.setDifficultyLevel(question.getDifficultyLevel());

        // Generic content image URL
        dto.setContentImageUrl(question.getContentImageUrl());

        // Category (multilingual)
        if (question.getCategory() != null) {
            dto.setCategoryId(question.getCategory().getId());
            dto.setCategoryCode(question.getCategory().getCode());
            dto.setCategoryNameEn(question.getCategory().getNameEn());
            dto.setCategoryNameAr(question.getCategory().getNameAr());
            dto.setCategoryNameNl(question.getCategory().getNameNl());
            dto.setCategoryNameFr(question.getCategory().getNameFr());
        }

        // Options: filter placeholder / corrupted translations, then shuffle
        // the delivery order so the correct answer is not fixed in one slot.
        if (question.getOptions() != null) {
            List<QuizAnswerOptionDTO> optionDTOs = question.getDeliverableOptions().stream()
                    .filter(option -> {
                        boolean placeholder = PlaceholderDetector.hasPlaceholder(
                                option.getOptionTextEn(), option.getOptionTextNl(),
                                option.getOptionTextFr(), option.getOptionTextAr());
                        if (placeholder) {
                            log.warn(
                                    "⚠️ Placeholder option filtered before DTO — question_id={}, option_id={}, text_en='{}'",
                                    question.getId(), option.getId(), option.getOptionTextEn());
                        }
                        return !placeholder;
                    })
                    .sorted(Comparator.comparing(
                            QuizAnswerOption::getDisplayOrder,
                            Comparator.nullsLast(Integer::compareTo)))
                    .map(this::toOptionDTO)
                    .collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(optionDTOs);
            for (int i = 0; i < optionDTOs.size(); i++) {
                optionDTOs.get(i).setDisplayOrder(i + 1);
            }
            dto.setOptions(optionDTOs);
        }

        return dto;
    }

    /**
     * Convert QuizAnswerOption entity to DTO
     * NOTE: isCorrect is NOT included (security - don't expose answers to client)
     */
    private QuizAnswerOptionDTO toOptionDTO(QuizAnswerOption option) {
        if (option == null) {
            return null;
        }

        QuizAnswerOptionDTO dto = new QuizAnswerOptionDTO();
        dto.setId(option.getId());
        dto.setOptionTextAr(roadSignReferenceTextResolver.resolveAr(option.getOptionTextAr()));
        dto.setOptionTextEn(roadSignReferenceTextResolver.resolveEn(option.getOptionTextEn()));
        dto.setOptionTextNl(roadSignReferenceTextResolver.resolveNl(option.getOptionTextNl()));
        dto.setOptionTextFr(roadSignReferenceTextResolver.resolveFr(option.getOptionTextFr()));
        dto.setDisplayOrder(option.getDisplayOrder());
        // isCorrect is NOT set (security - client should not know correct answer)

        return dto;
    }

    /**
     * Convert list of questions to DTOs
     */
    public List<QuizQuestionDTO> toDTOList(List<QuizQuestion> questions) {
        if (questions == null) {
            return null;
        }

        return questions.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
