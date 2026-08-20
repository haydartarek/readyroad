package com.readyroad.readyroadbackend.dto.response;

import java.time.LocalDateTime;
import java.math.BigDecimal;

public record AdminSystemSettingsResponse(
        String siteName,
        String defaultLanguage,
        boolean maintenanceMode,
        boolean allowRegistrations,
        int examQuestions,
        BigDecimal examDurationMinutes,
        int passingScorePercent,
        boolean siteNameEditable,
        boolean examSettingsEditable,
        LocalDateTime updatedAt) {
}
