package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class EditorialArticleImagePolicyTest {

    private static final Instant NOW = Instant.parse("2026-08-24T00:00:00Z");
    private final EditorialArticleImagePolicy policy =
            new EditorialArticleImagePolicy(Clock.fixed(NOW, ZoneOffset.UTC));

    @Test
    void acceptsOnlyAnApprovedProviderUrlWithCompleteLocalizedMetadata() {
        var normalized = policy.normalize(
                file("image/jpeg"),
                metadata("https://unsplash.com/photos/belgian-road-123"),
                "admin@rijvia.be");

        assertThat(normalized.sourcePlatform())
                .isEqualTo(EditorialArticleImageDtos.SourcePlatform.UNSPLASH);
        assertThat(normalized.altTextAr()).isEqualTo("تقاطع طريق في بلجيكا");
        assertThat(normalized.altTextEn()).isEqualTo("A road junction in Belgium");
        assertThat(normalized.focalPointX()).isEqualTo(0.5);
        assertThat(normalized.approvedBy()).isEqualTo("admin@rijvia.be");
    }

    @Test
    void rejectsAProviderMismatchInsteadOfAcceptingUnknownLicensing() {
        assertThatThrownBy(() -> policy.normalize(
                file("image/jpeg"),
                metadata("https://example.com/photos/belgian-road-123"),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sourceUrl");
    }

    @Test
    void rejectsSpoofedOrOversizedUploadContractsBeforeProcessing() {
        assertThatThrownBy(() -> policy.normalize(
                file("image/gif"),
                metadata("https://unsplash.com/photos/belgian-road-123"),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPEG or PNG");

        var oversized = new MockMultipartFile(
                "file", "road.jpg", "image/jpeg",
                new byte[(int) EditorialArticleImagePolicy.MAX_UPLOAD_BYTES + 1]);
        assertThatThrownBy(() -> policy.normalize(
                oversized,
                metadata("https://unsplash.com/photos/belgian-road-123"),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("20 MB");
    }

    private static MockMultipartFile file(String contentType) {
        return new MockMultipartFile("file", "belgian-road.jpg", contentType, new byte[] {1, 2, 3});
    }

    private static EditorialArticleImageDtos.UploadMetadata metadata(String sourceUrl) {
        return new EditorialArticleImageDtos.UploadMetadata(
                EditorialArticleImageDtos.SourcePlatform.UNSPLASH,
                "belgian-road-123",
                sourceUrl,
                "Road Photographer",
                "https://unsplash.com/@road-photographer",
                "Unsplash License",
                "https://unsplash.com/license",
                NOW.minusSeconds(3600),
                NOW.minusSeconds(1800),
                "تقاطع طريق في بلجيكا",
                "Een kruispunt in België",
                "Un carrefour routier en Belgique",
                "A road junction in Belgium",
                null,
                null,
                null,
                null,
                null,
                null,
                "License, relevance and privacy reviewed by the administrator");
    }
}
