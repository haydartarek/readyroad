package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoadSignReferenceTextResolverTest {

    @Mock
    private RoadSignRepository roadSignRepository;

    private RoadSignReferenceTextResolver resolver;

    @BeforeEach
    void setUp() {
        when(roadSignRepository.findAllByIsActiveTrue()).thenReturn(List.of(
                sign("C31a", "No left turn", "ممنوع الانعطاف إلى اليسار", "Interdiction de tourner à gauche",
                        "Verbod links afslaan", null),
                sign("B11", "End of priority road", "نهاية طريق الأولوية",
                        "Fin de route prioritaire", "Einde van de voorrangsweg", null),
                sign("B17", "Crossroads where priority from the right applies",
                        "تقاطع تسري فيه أولوية اليمين", "Carrefour où la priorité de droite s'applique",
                        "Kruispunt waar voorrang van rechts geldt", null),
                sign("C22", "No entry for coaches", "ممنوع مرور الحافلات", "Acces interdit aux autocars",
                        "Verboden voor autocars",
                        "/images/signs/prohibition_signs/C22 Verboden toegang voor bestuurders van autocars.png"),
                sign("C43", "Speed limit 50 km/h", "السرعة القصوى 50 كم/س",
                        "Limitation de vitesse 50 km/h",
                        "Verbod te rijden met een grotere snelheid dan 50 km/u", null),
                sign("E1", "No parking", "ممنوع الوقوف", "Stationnement interdit",
                        "Parkeerverbod", null),
                sign("D4-rechts", "Mandatory right turn for vehicles carrying dangerous goods",
                        "اتجاه إلزامي إلى اليمين للمركبات التي تنقل بضائع خطرة",
                        "Direction obligatoire a droite pour les vehicules transportant des marchandises dangereuses",
                        "Verplicht rechts afslaan gevaarlijke goederen",
                        "/images/signs/mandatory_signs/D4 Verplicht rechts afslaan gevaarlijke goederen.png")));
        resolver = new RoadSignReferenceTextResolver(
                roadSignRepository,
                new ObjectMapper(),
                new DefaultResourceLoader());
    }

    @Test
    void replacesCodesWithLocalizedNames() {
        String resolved = resolver.resolveEn("May you turn left at sign C31a if the road is clear?");

        assertTrue(resolved.contains("No left turn"));
        assertFalse(resolved.contains("C31a"));
    }

    @Test
    void keepsEmbeddedFragmentsUntouched() {
        String resolved = resolver.resolveEn("Compare XC43 with C43 before answering.");

        assertTrue(resolved.contains("XC43"));
        assertTrue(resolved.contains("Speed limit 50 km/h"));
    }

    @Test
    void resolvesCustomAliasWithoutCanonicalSignCode() {
        String resolved = resolver.resolveEn("What is the difference between M2 and M3 Belgian delineation markers?");

        assertTrue(resolved.contains("type 2 delineation markers"));
        assertTrue(resolved.contains("type 3 delineation markers"));
        assertFalse(resolved.contains(" M2 "));
        assertFalse(resolved.contains(" M3 "));
    }

    @Test
    void resolvesCompoundTokenUsingBaseSignName() {
        String resolved = resolver.resolveNl("Er is een E1-bord. Waar parkeert u?");

        assertTrue(resolved.contains("het bord Parkeerverbod"), resolved);
        assertFalse(resolved.contains("E1-bord"));
    }

    @Test
    void resolvesLegacyDirectionalCompoundThroughAliasNormalization() {
        String resolved = resolver.resolveEn("What is the purpose of D4-rechts?");

        assertTrue(resolved.contains("Mandatory right turn for vehicles carrying dangerous goods"), resolved);
        assertFalse(resolved.contains("D4-rechts"));
    }

    @Test
    void resolvesM4CustomAlias() {
        String resolved = resolver.resolveEn("Does M4 warn about the road edge?");

        assertTrue(resolved.contains("delineation marker with alternating red and white stripes"));
        assertFalse(resolved.contains(" M4 "));
    }

    @Test
    void replacesMultiplePriorityReferencesWithSignNames() {
        String resolved = resolver.resolveEn(
                "After sign B11 you approach a junction with sign B17. Do you have right-of-way from the right at that junction?");

        assertTrue(resolved.contains("End of priority road"), resolved);
        assertTrue(resolved.contains("Crossroads where priority from the right applies"), resolved);
        assertFalse(resolved.contains(" B11 "), resolved);
        assertFalse(resolved.contains(" B17 "), resolved);
    }

    private static RoadSign sign(String code, String en, String ar, String fr, String nl, String imagePath) {
        RoadSign sign = new RoadSign();
        sign.setSignCode(code);
        sign.setNameEn(en);
        sign.setNameAr(ar);
        sign.setNameFr(fr);
        sign.setNameNl(nl);
        sign.setImagePath(imagePath);
        sign.setIsActive(true);
        return sign;
    }
}
