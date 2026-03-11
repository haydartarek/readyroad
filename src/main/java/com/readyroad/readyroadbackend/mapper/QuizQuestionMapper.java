package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizAnswerOptionDTO;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import com.readyroad.readyroadbackend.util.PlaceholderDetector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
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
 * - SmartQuizService (when fully restored)
 * - Tests (ContentSwapProofTest)
 *
 * @see com.readyroad.readyroadbackend.dto.QuizQuestionDTO
 * @see com.readyroad.readyroadbackend.domain.entity.QuizQuestion
 */
@Component
@Slf4j
public class QuizQuestionMapper {

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
        dto.setQuestionAr(question.getQuestionAr());
        dto.setQuestionEn(question.getQuestionEn());
        dto.setQuestionNl(question.getQuestionNl());
        dto.setQuestionFr(question.getQuestionFr());

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

        // Options: filter placeholder / corrupted translations, then shuffle.
        // Correct answer identity is always tracked by stable option ID, never by
        // visual position, so filtering here is safe.
        if (question.getOptions() != null) {
            List<QuizAnswerOptionDTO> optionDTOs = question.getOptions().stream()
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
                    .map(this::toOptionDTO)
                    .collect(Collectors.toCollection(ArrayList::new));
            Collections.shuffle(optionDTOs);
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
        dto.setOptionTextAr(option.getOptionTextAr());
        dto.setOptionTextEn(option.getOptionTextEn());
        dto.setOptionTextNl(option.getOptionTextNl());
        dto.setOptionTextFr(option.getOptionTextFr());
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
