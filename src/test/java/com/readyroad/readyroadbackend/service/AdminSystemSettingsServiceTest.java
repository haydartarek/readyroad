package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.AdminSystemSettings;
import com.readyroad.readyroadbackend.domain.repository.AdminSystemSettingsRepository;
import com.readyroad.readyroadbackend.dto.AdminSystemSettingsUpdateRequest;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminSystemSettingsServiceTest {

    @Mock
    private AdminSystemSettingsRepository settingsRepository;

    @InjectMocks
    private AdminSystemSettingsService service;

    @Test
    void reportsExamConfigurationAsReadOnly() {
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(settings()));

        var response = service.getSettings();

        assertThat(response.siteNameEditable()).isFalse();
        assertThat(response.siteName()).isEqualTo("RijVia");
        assertThat(response.examSettingsEditable()).isFalse();
        assertThat(response.examQuestions()).isEqualTo(50);
        assertThat(response.examDurationMinutes()).isEqualByComparingTo("12.50");
        assertThat(response.passingScorePercent()).isEqualTo(82);
    }

    @Test
    void rejectsUnsupportedExamRuleChanges() {
        var request = new AdminSystemSettingsUpdateRequest(
                "RijVia", "en", false, true, 40, new BigDecimal("12.50"), 82);

        assertThatThrownBy(() -> service.updateSettings(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("50 questions");
        verify(settingsRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsUnsupportedSiteNameChanges() {
        var request = new AdminSystemSettingsUpdateRequest(
                "Another product", "en", false, true, 50, new BigDecimal("12.50"), 82);

        assertThatThrownBy(() -> service.updateSettings(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fixed and read-only");
        verify(settingsRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void returnsConfiguredDefaultLanguage() {
        AdminSystemSettings settings = settings();
        settings.setDefaultLanguage("fr");
        when(settingsRepository.findFirstByOrderByIdAsc()).thenReturn(Optional.of(settings));

        assertThat(service.getDefaultLanguage()).isEqualTo("fr");
    }

    private AdminSystemSettings settings() {
        AdminSystemSettings settings = new AdminSystemSettings();
        settings.setSiteName("RijVia");
        settings.setDefaultLanguage("en");
        settings.setMaintenanceMode(false);
        settings.setAllowRegistrations(true);
        settings.setExamQuestions(50);
        settings.setExamDurationMinutes(new BigDecimal("12.50"));
        settings.setPassingScorePercent(82);
        return settings;
    }
}
