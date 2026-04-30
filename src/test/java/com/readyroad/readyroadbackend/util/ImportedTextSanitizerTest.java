package com.readyroad.readyroadbackend.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImportedTextSanitizerTest {

    @Test
    void repairsArabicMojibakeIntoReadableArabic() {
        String value = "Ø·Ø±ÙŠÙ‚ Ø¥Ø¬Ø¨Ø§Ø±ÙŠ Ù…Ø®ØµØµ Ù„Ø±Ø§ÙƒØ¨ÙŠ Ø§Ù„Ø®ÙŠÙ„";

        String sanitized = ImportedTextSanitizer.sanitize(value);

        assertThat(sanitized).isEqualTo("طريق إجباري مخصص لراكبي الخيل");
    }

    @Test
    void repairsFrenchMojibakeIntoReadableFrench() {
        String value = "Les piÃ©tons et les vÃ©hicules ne peuvent pas utiliser ce chemin.";

        String sanitized = ImportedTextSanitizer.sanitize(value);

        assertThat(sanitized).isEqualTo("Les piétons et les véhicules ne peuvent pas utiliser ce chemin.");
    }

    @Test
    void flagsImportedTextThatNeedsRepair() {
        assertThat(ImportedTextSanitizer.requiresRepair("Ù†Ù‡Ø§ÙŠØ© Ù…Ù†Ø·Ù‚Ø© Ø´Ø§Ø±Ø¹ Ø§Ù„Ø¯Ø±Ø§Ø¬Ø§Øª"))
                .isTrue();
        assertThat(ImportedTextSanitizer.requiresRepair("Zone accÃ¨s interdit aux vÃ©hicules"))
                .isTrue();
    }

    @Test
    void doesNotFlagAlreadyCleanImportedText() {
        assertThat(ImportedTextSanitizer.requiresRepair("نهاية منطقة شارع الدراجات"))
                .isFalse();
        assertThat(ImportedTextSanitizer.requiresRepair("Zone accès interdit aux véhicules"))
                .isFalse();
    }
}
