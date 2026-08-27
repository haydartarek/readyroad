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
    void acceptsAConfirmedLocalUploadWithCompleteLocalizedMetadata() {
        var normalized = policy.normalize(
                file("image/jpeg"),
                metadata(true, "https://rijvia.be/image-sources/belgian-road-123"),
                "admin@rijvia.be");

        assertThat(normalized.sourcePlatform())
                .isEqualTo(EditorialArticleImageDtos.SourcePlatform.LOCAL_UPLOAD);
        assertThat(normalized.altTextAr()).isEqualTo("تقاطع طريق في بلجيكا");
        assertThat(normalized.altTextEn()).isEqualTo("A road junction in Belgium");
        assertThat(normalized.storedFileName()).isEqualTo("rijvia-en-belgian-road-hero");
        assertThat(normalized.licenseName()).isEqualTo("Owner-approved local file");
        assertThat(normalized.focalPointX()).isEqualTo(0.5);
        assertThat(normalized.approvedBy()).isEqualTo("admin@rijvia.be");
    }

    @Test
    void rejectsUnconfirmedRightsAndUnsafeOptionalSourceUrls() {
        assertThatThrownBy(() -> policy.normalize(
                file("image/jpeg"),
                metadata(false, null),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("rights");

        assertThatThrownBy(() -> policy.normalize(
                file("image/jpeg"),
                metadata(true, "http://example.com/source"),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("sourceUrl");
    }

    @Test
    void rejectsSpoofedOrOversizedUploadContractsBeforeProcessing() {
        assertThatThrownBy(() -> policy.normalize(
                file("image/gif"),
                metadata(true, null),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPEG or PNG");

        var oversized = new MockMultipartFile(
                "file", "road.jpg", "image/jpeg",
                new byte[(int) EditorialArticleImagePolicy.MAX_UPLOAD_BYTES + 1]);
        assertThatThrownBy(() -> policy.normalize(
                oversized,
                metadata(true, null),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 MB");
    }

    private static MockMultipartFile file(String contentType) {
        return new MockMultipartFile("file", "belgian-road.jpg", contentType, new byte[] {1, 2, 3});
    }

    private static EditorialArticleImageDtos.UploadMetadata metadata(
            boolean rightsConfirmed,
            String sourceUrl) {
        return new EditorialArticleImageDtos.UploadMetadata(
                "RijVia EN Belgian road hero.JPG",
                "RijVia owner upload",
                sourceUrl,
                "Owner-approved local file",
                null,
                "Usage rights and relevance verified by the administrator",
                rightsConfirmed,
                "تقاطع طريق في بلجيكا",
                "Een kruispunt in België",
                "Un carrefour routier en Belgique",
                "A road junction in Belgium",
                null,
                null,
                null,
                null,
                0.5,
                0.5
        );
    }
}
