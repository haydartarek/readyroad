package com.readyroad.readyroadbackend.marketing.youtube;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class YouTubeContentPackageServiceTest {

    @Test
    void classifiesVerifiedSourceMetadataWithoutGeneratingLegalClaims() {
        assertThat(YouTubeContentPackageService.classifyPillar("شرح إشارات الأولوية"))
                .isEqualTo("PRIORITY_INTERSECTIONS");
        assertThat(YouTubeContentPackageService.classifyPillar("علامات الوقوف والتوقف"))
                .isEqualTo("SPEED_PARKING_STOPPING");
        assertThat(YouTubeContentPackageService.classifyPillar("اختبار سريع"))
                .isEqualTo("TRAINING_TESTS");
        assertThat(YouTubeContentPackageService.classifyPillar("ReadyRoad update"))
                .isEqualTo("READYROAD_EDUCATIONAL_VIDEOS");
    }

    @Test
    void removesEmojiFromRedistributionDrafts() {
        assertThat(YouTubeContentPackageService.clean("امتحان 2026 ⚠️ شرح واضح"))
                .isEqualTo("امتحان 2026 شرح واضح")
                .doesNotContain("⚠", "️");
    }
}
