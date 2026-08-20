package com.readyroad.readyroadbackend.dto.exam;

import java.util.List;

/** Immutable content shown by one theory-exam question at exam creation time. */
public record TheoryExamQuestionSnapshot(
        short version,
        Long questionId,
        LocalizedText questionText,
        LocalizedText explanation,
        String contentImageUrl,
        CategorySnapshot category,
        String difficulty,
        List<OptionSnapshot> options) {

    public record LocalizedText(
            String en,
            String nl,
            String fr,
            String ar) {
    }

    public record CategorySnapshot(
            Long id,
            String code,
            LocalizedText name) {
    }

    public record OptionSnapshot(
            Long id,
            LocalizedText text,
            boolean correct,
            Integer displayOrder) {
    }
}
