package com.readyroad.readyroadbackend.dto.sign;

import java.util.List;

public record SignExamHistoryResponseDto(
        int totalResults,
        List<SignExamHistoryItemDto> results) {
}
