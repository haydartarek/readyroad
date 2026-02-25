package com.readyroad.readyroadbackend.mapper;

import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.dto.QuizAnswerOptionDTO;
import com.readyroad.readyroadbackend.dto.QuizQuestionDTO;
import org.springframework.stereotype.Component;

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
 * - SmartQuizService (when fully restored)
 * - Tests (ContentSwapProofTest)
 *
 * @see com.readyroad.readyroadbackend.dto.QuizQuestionDTO
 * @see com.readyroad.readyroadbackend.domain.entity.QuizQuestion
 */
@Component
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

        // Options sorted by displayOrder — NO correctness signals exposed
        if (question.getOptions() != null) {
            List<QuizAnswerOptionDTO> optionDTOs = question.getOptions().stream()
                    .sorted(Comparator.comparingInt(o -> o.getDisplayOrder() != null ? o.getDisplayOrder() : 0))
                    .map(this::toOptionDTO)
                    .collect(Collectors.toList());
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
