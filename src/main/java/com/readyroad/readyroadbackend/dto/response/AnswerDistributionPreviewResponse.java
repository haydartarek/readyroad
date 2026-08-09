package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record AnswerDistributionPreviewResponse(
        int selectedQuestions,
        List<GroupDistribution> before,
        List<GroupDistribution> after) {

    public record GroupDistribution(
            String difficulty,
            int optionCount,
            int total,
            List<CorrectAnswerDistributionResponse.AnswerPosition> positions) {
    }
}
