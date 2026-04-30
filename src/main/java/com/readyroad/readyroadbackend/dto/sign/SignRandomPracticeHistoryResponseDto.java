package com.readyroad.readyroadbackend.dto.sign;

import java.util.List;

public record SignRandomPracticeHistoryResponseDto(
        int totalSessions,
        List<SignRandomPracticeHistoryItemDto> sessions) {
}
