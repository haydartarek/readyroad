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

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSystemSettingsService {

    private final AdminSystemSettingsRepository settingsRepository;

    @Transactional(readOnly = true)
    public AdminSystemSettingsResponse getSettings() {
        return toResponse(getOrCreateSettings());
    }

    @Transactional
    public AdminSystemSettingsResponse updateSettings(AdminSystemSettingsUpdateRequest request) {
        AdminSystemSettings settings = getOrCreateSettings();
        settings.setSiteName(request.siteName().trim());
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
        settings.setSiteName("ReadyRoad");
        settings.setDefaultLanguage("en");
        settings.setMaintenanceMode(false);
        settings.setAllowRegistrations(true);
        settings.setExamQuestions(50);
        settings.setExamDurationMinutes(30);
        settings.setPassingScorePercent(82);
        return settings;
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
                settings.getUpdatedAt());
    }
}
