package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record CorrectAnswerDistributionResponse(
        long total,
        List<AnswerPosition> positions) {

    public record AnswerPosition(String label, long count, double percentage) {
    }
}
