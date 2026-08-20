package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.AdminSystemSettings;
import com.readyroad.readyroadbackend.domain.repository.AdminSystemSettingsRepository;
import com.readyroad.readyroadbackend.dto.AdminSystemSettingsUpdateRequest;
import com.readyroad.readyroadbackend.dto.response.AdminSystemSettingsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSystemSettingsService {

    public static final String FIXED_SITE_NAME = "RijVia";
    public static final int FIXED_EXAM_QUESTION_COUNT = 50;
    public static final BigDecimal FIXED_EXAM_DURATION_MINUTES =
            TheoryExamTiming.totalMinutes(FIXED_EXAM_QUESTION_COUNT);
    public static final int FIXED_PASSING_SCORE_PERCENT = 82;

    private final AdminSystemSettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    public AdminSystemSettingsResponse getSettings() {
        return toResponse(getOrCreateSettings());
    }

    @Transactional
    public AdminSystemSettingsResponse updateSettings(AdminSystemSettingsUpdateRequest request) {
        validateFixedSiteName(request);
        validateFixedExamConfiguration(request);
        AdminSystemSettings settings = getOrCreateSettings();
        settings.setDefaultLanguage(request.defaultLanguage().trim());
        settings.setMaintenanceMode(request.maintenanceMode());
        settings.setAllowRegistrations(request.allowRegistrations());
        settings.setExamQuestions(request.examQuestions());
        settings.setExamDurationMinutes(request.examDurationMinutes());
        settings.setPassingScorePercent(request.passingScorePercent());
        AdminSystemSettings saved = settingsRepository.save(settings);
        log.info("✅ Admin system settings updated");
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public boolean isMaintenanceModeEnabled() {
        return Boolean.TRUE.equals(getOrCreateSettings().getMaintenanceMode());
    }

    @Transactional(readOnly = true)
    public boolean areRegistrationsAllowed() {
        return Boolean.TRUE.equals(getOrCreateSettings().getAllowRegistrations());
    }

    @Transactional(readOnly = true)
    public String getDefaultLanguage() {
        return getOrCreateSettings().getDefaultLanguage();
    }

    @Transactional
    public AdminSystemSettings getOrCreateSettings() {
        return settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(this::createDefaultSettingsSafely);
    }

    private AdminSystemSettings createDefaultSettingsSafely() {
        try {
            return settingsRepository.save(defaultSettings());
        } catch (DataIntegrityViolationException | ObjectOptimisticLockingFailureException ex) {
            log.warn("Admin settings already created in a concurrent transaction. Reloading existing row.");
            return settingsRepository.findFirstByOrderByIdAsc()
                    .orElseThrow(() -> ex);
        }
    }

    private AdminSystemSettings defaultSettings() {
        AdminSystemSettings settings = new AdminSystemSettings();
        settings.setSiteName(FIXED_SITE_NAME);
        settings.setDefaultLanguage("en");
        settings.setMaintenanceMode(false);
        settings.setAllowRegistrations(true);
        settings.setExamQuestions(FIXED_EXAM_QUESTION_COUNT);
        settings.setExamDurationMinutes(FIXED_EXAM_DURATION_MINUTES);
        settings.setPassingScorePercent(FIXED_PASSING_SCORE_PERCENT);
        return settings;
    }

    private void validateFixedExamConfiguration(AdminSystemSettingsUpdateRequest request) {
        if (!Integer.valueOf(FIXED_EXAM_QUESTION_COUNT).equals(request.examQuestions())
                || request.examDurationMinutes() == null
                || FIXED_EXAM_DURATION_MINUTES.compareTo(request.examDurationMinutes()) != 0
                || !Integer.valueOf(FIXED_PASSING_SCORE_PERCENT).equals(request.passingScorePercent())) {
            throw new IllegalArgumentException(
                    "The theory exam configuration is fixed at 50 questions, 15 seconds per question, and an 82% passing score.");
        }
    }

    private void validateFixedSiteName(AdminSystemSettingsUpdateRequest request) {
        if (!FIXED_SITE_NAME.equals(request.siteName().trim())) {
            throw new IllegalArgumentException("The RijVia product name is fixed and read-only.");
        }
    }

    private AdminSystemSettingsResponse toResponse(AdminSystemSettings settings) {
        return new AdminSystemSettingsResponse(
                settings.getSiteName(),
                settings.getDefaultLanguage(),
                Boolean.TRUE.equals(settings.getMaintenanceMode()),
                Boolean.TRUE.equals(settings.getAllowRegistrations()),
                settings.getExamQuestions(),
                settings.getExamDurationMinutes(),
                settings.getPassingScorePercent(),
                false,
                false,
                settings.getUpdatedAt());
    }
}
