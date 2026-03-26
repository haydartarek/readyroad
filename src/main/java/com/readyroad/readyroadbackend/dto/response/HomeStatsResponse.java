package com.readyroad.readyroadbackend.dto.response;

public record HomeStatsResponse(
        long examQuestionCount,
        long trafficSignsCount,
        long lessonsCount,
        long categoriesCount,
        long supportedLanguagesCount) {
}
