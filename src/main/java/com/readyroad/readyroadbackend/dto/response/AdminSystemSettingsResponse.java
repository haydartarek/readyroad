package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;

public record AdminSystemSettingsResponse(
        String siteName,
        String defaultLanguage,
        boolean maintenanceMode,
        boolean allowRegistrations,
        int examQuestions,
        int examDurationMinutes,
        int passingScorePercent,
        LocalDateTime updatedAt) {
}
