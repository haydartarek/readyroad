package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.enums.SignCategory;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression guard for Traffic Sign A1a data integrity.
 *
 * <p>
 * Ensures that A1a canonical data is correctly stored, retrieved, and
 * rendered through the full service to mapper to DTO pipeline, with no
 * escaped unicode sequences (backslash-uXXXX) in any multilingual field.
 * </p>
 *
 * <p>
 * Seeds its own A1a data into H2 to mirror what V86 does in production.
 * </p>
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Feature: Traffic Sign A1a Data Integrity Regression Guard")
class TrafficSignA1aRegressionTest {

    // ── Canonical V6 / V86 values ──────────────────────
    private static final String SIGN_CODE = "A1a";
    private static final String CATEGORY_CODE = "A";
    // Values come from signs.json canonical catalog (title_* /
    // long_description_*_official fields)
    private static final String NAME_EN = "Dangerous bend to the left";
    private static final String NAME_AR = "منعطف خطير إلى اليسار";
    private static final String NAME_NL = "Gevaarlijke bocht naar links";
    private static final String NAME_FR = "Virage dangereux à gauche";
    private static final String DESC_EN = "Warns of a dangerous bend to the left where loss of control or reduced visibility may occur, especially at higher speeds.";
    private static final String DESC_AR = "تحذير من منعطف خطير إلى اليسار حيث قد يحدث فقدان للسيطرة أو تتراجع الرؤية، خصوصاً عند السرعات العالية.";
    private static final String DESC_NL = "Waarschuwt voor een gevaarlijke bocht naar links waar de weg scherp van richting verandert en verlies van controle of beperkt zicht kan optreden, vooral bij hogere snelheid.";
    private static final String DESC_FR = "Avertit d'un virage dangereux à gauche où une perte de contrôle ou une visibilité réduite peut se produire, surtout à vitesse élevée.";

    /**
     * Detects literal escaped unicode sequences like \\u0639 or \\u00E9 in
     * strings.
     */
    private static final Pattern ESCAPED_UNICODE = Pattern.compile("\\\\u[0-9A-Fa-f]{4}");

    @Autowired
    private TrafficSignService trafficSignService;

    @Autowired
    private RoadSignRepository RoadSignRepository;

    private RoadSign a1a;

    @BeforeEach
    void seedA1a() {
        // If A1a already exists (from another test), delete it to start fresh
        RoadSignRepository.findBySignCode(SIGN_CODE)
                .ifPresent(RoadSignRepository::delete);
        RoadSignRepository.flush();

        // Insert A1a with canonical V86 data
        a1a = new RoadSign();
        a1a.setSignCode(SIGN_CODE);
        a1a.setNormalizedSignCode(SIGN_CODE.toLowerCase());
        a1a.setCategory(SignCategory.DANGER);
        a1a.setNameEn(NAME_EN);
        a1a.setNameAr(NAME_AR);
        a1a.setNameNl(NAME_NL);
        a1a.setNameFr(NAME_FR);
        a1a.setDescriptionEn(DESC_EN);
        a1a.setDescriptionAr(DESC_AR);
        a1a.setDescriptionNl(DESC_NL);
        a1a.setDescriptionFr(DESC_FR);
        a1a.setImagePath("images/signs/danger_signs/A1a.png");
        a1a.setIsActive(true);
        a1a = RoadSignRepository.saveAndFlush(a1a);
    }

    // ── Scenario: API response correctness for A1a ─────

    @Nested
    @DisplayName("Scenario: Contract verification — API response correctness for A1a")
    class ContractVerification {

        @Test
        @DisplayName("Given A1a exists, When getSignByCode is called, Then response contains canonical names")
        void a1a_returns_canonical_names() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            assertThat(response.signCode()).isEqualTo(SIGN_CODE);
            assertThat(response.nameEn()).isEqualTo(NAME_EN);
            assertThat(response.nameAr()).isEqualTo(NAME_AR);
            assertThat(response.nameNl()).isEqualTo(NAME_NL);
            assertThat(response.nameFr()).isEqualTo(NAME_FR);
        }

        @Test
        @DisplayName("Given A1a exists, When getSignByCode is called, Then all 4 descriptions are non-empty")
        void a1a_has_non_empty_descriptions() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            assertThat(response.descriptionEn()).isNotBlank().isEqualTo(DESC_EN);
            assertThat(response.descriptionAr()).isNotBlank().isEqualTo(DESC_AR);
            assertThat(response.descriptionNl()).isNotBlank().isEqualTo(DESC_NL);
            assertThat(response.descriptionFr()).isNotBlank().isEqualTo(DESC_FR);
        }

        @Test
        @DisplayName("Given A1a in road_signs without long desc, When getSignByCode is called, Then catalog enriches with long descriptions")
        void a1a_has_no_long_descriptions_in_road_signs() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            // signs.json provides long descriptions for A1a — catalog enriches even when
            // road_signs row has none
            assertThat(response.longDescriptionEn()).isNotBlank();
            assertThat(response.longDescriptionNl()).isNotBlank();
            assertThat(response.longDescriptionFr()).isNotBlank();
            assertThat(response.longDescriptionAr()).isNotBlank();
            assertThat(response.isLongDescriptionComplete()).isTrue();
        }

        @Test
        @DisplayName("Given A1a exists, When getSignByCode is called, Then category is A (Warning/Danger)")
        void a1a_has_correct_category() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            assertThat(response.categoryCode()).isEqualTo(CATEGORY_CODE);
        }

        @Test
        @DisplayName("Given A1a isActive=true, Then it appears in getAllActiveSigns")
        void a1a_is_active_and_delivered() {
            List<TrafficSignResponse> allActive = trafficSignService.getAllActiveSigns();
            assertThat(allActive)
                    .extracting(TrafficSignResponse::signCode)
                    .contains(SIGN_CODE);
        }
    }

    // ── Scenario: No escaped unicode in multilingual fields ─

    @Nested
    @DisplayName("Scenario: Regression guard — no escaped unicode in response fields")
    class UnicodeRegressionGuard {

        @Test
        @DisplayName("Given A1a response, Then no field contains escaped backslash-u sequences")
        void a1a_response_has_no_escaped_unicode() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            // Check every multilingual field for escaped unicode sequences
            assertNoEscapedUnicode("nameEn", response.nameEn());
            assertNoEscapedUnicode("nameAr", response.nameAr());
            assertNoEscapedUnicode("nameNl", response.nameNl());
            assertNoEscapedUnicode("nameFr", response.nameFr());
            assertNoEscapedUnicode("descriptionEn", response.descriptionEn());
            assertNoEscapedUnicode("descriptionAr", response.descriptionAr());
            assertNoEscapedUnicode("descriptionNl", response.descriptionNl());
            assertNoEscapedUnicode("descriptionFr", response.descriptionFr());
            assertNoEscapedUnicode("longDescriptionEn", response.longDescriptionEn());
            assertNoEscapedUnicode("longDescriptionAr", response.longDescriptionAr());
            assertNoEscapedUnicode("longDescriptionNl", response.longDescriptionNl());
            assertNoEscapedUnicode("longDescriptionFr", response.longDescriptionFr());
            assertNoEscapedUnicode("categoryCode", response.categoryCode());
        }

        @Test
        @DisplayName("Given A1a response, Then all multilingual fields are valid UTF-8 strings")
        void a1a_response_fields_are_valid_utf8() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            // Arabic fields must contain Arabic characters (U+0600..U+06FF range)
            assertThat(response.nameAr())
                    .as("nameAr must contain Arabic script characters")
                    .matches(".*[\u0600-\u06FF].*");
            assertThat(response.descriptionAr())
                    .as("descriptionAr must contain Arabic script characters")
                    .matches(".*[\u0600-\u06FF].*");

            // French fields with accented characters must contain actual accented chars
            assertThat(response.nameFr()).contains("à");
            assertThat(response.descriptionFr()).contains("à");
        }

        @Test
        @DisplayName("Given all active signs, Then no sign has escaped unicode in any field")
        void no_active_sign_has_escaped_unicode() {
            List<TrafficSignResponse> allSigns = trafficSignService.getAllActiveSigns();
            assertThat(allSigns).isNotEmpty();

            for (TrafficSignResponse sign : allSigns) {
                String prefix = "sign[" + sign.signCode() + "].";
                assertNoEscapedUnicode(prefix + "nameEn", sign.nameEn());
                assertNoEscapedUnicode(prefix + "nameAr", sign.nameAr());
                assertNoEscapedUnicode(prefix + "nameNl", sign.nameNl());
                assertNoEscapedUnicode(prefix + "nameFr", sign.nameFr());
                if (sign.descriptionEn() != null)
                    assertNoEscapedUnicode(prefix + "descriptionEn", sign.descriptionEn());
                if (sign.descriptionAr() != null)
                    assertNoEscapedUnicode(prefix + "descriptionAr", sign.descriptionAr());
                if (sign.descriptionNl() != null)
                    assertNoEscapedUnicode(prefix + "descriptionNl", sign.descriptionNl());
                if (sign.descriptionFr() != null)
                    assertNoEscapedUnicode(prefix + "descriptionFr", sign.descriptionFr());
            }
        }
    }

    // ── Scenario: A1a is not confused with siblings ────

    @Nested
    @DisplayName("Scenario: A1a is distinct from A1b (no content swap)")
    class ContentSwapGuard {

        @Test
        @DisplayName("Given A1a, Then names do NOT contain 'right' or 'droite' or 'rechts' or 'لليمين'")
        void a1a_does_not_contain_right_direction_content() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            // A1a = left bend. Must NOT contain right-direction text (that's A1b).
            assertThat(response.nameEn().toLowerCase()).doesNotContain("right");
            assertThat(response.nameFr().toLowerCase()).doesNotContain("droite");
            assertThat(response.nameNl().toLowerCase()).doesNotContain("rechts");
            assertThat(response.nameAr()).doesNotContain("لليمين");

            // Must contain left-direction text
            assertThat(response.nameEn().toLowerCase()).contains("left");
            assertThat(response.nameFr().toLowerCase()).contains("gauche");
            assertThat(response.nameNl().toLowerCase()).contains("links");
            assertThat(response.nameAr()).contains("اليسار"); // matches لليسار and إلى اليسار
        }

        @Test
        @DisplayName("Given A1a, Then names are NOT 'Speed bump' / 'مطب' (the previous wrong content)")
        void a1a_is_not_the_old_wrong_content() {
            TrafficSignResponse response = trafficSignService.getSignByCode(SIGN_CODE);

            assertThat(response.nameEn()).isNotEqualToIgnoringCase("Speed bump");
            assertThat(response.nameAr()).isNotEqualTo("مطب");
            assertThat(response.nameNl()).isNotEqualToIgnoringCase("Verkeersdrempel");
            assertThat(response.nameFr()).isNotEqualToIgnoringCase("Ralentisseur");
        }
    }

    // ── Helper ─────────────────────────────────────────

    private void assertNoEscapedUnicode(String fieldName, String value) {
        if (value == null)
            return;
        assertThat(ESCAPED_UNICODE.matcher(value).find())
                .as("Field '%s' must not contain escaped unicode sequences, but was: %s", fieldName, value)
                .isFalse();
    }
}
