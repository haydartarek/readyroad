package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.context.support.ResourceBundleMessageSource;
import com.readyroad.readyroadbackend.service.BackendMessageService;

class EditorialArticleImagePolicyTest {

    private final EditorialArticleImagePolicy policy = policy();

    private static EditorialArticleImagePolicy policy() {
        var source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return new EditorialArticleImagePolicy(new BackendMessageService(source));
    }

    @Test
    void acceptsLocalUploadWithSeoNameAndLocalizedAltText() {
        var normalized = policy.normalize(
                file("image/jpeg"),
                metadata(),
                "admin@rijvia.be");

        assertThat(normalized.altTextAr()).isEqualTo("تقاطع طريق في بلجيكا");
        assertThat(normalized.altTextEn()).isEqualTo("A road junction in Belgium");
        assertThat(normalized.storedFileName()).isEqualTo("rijvia-en-belgian-road-hero-jpg");
        assertThat(normalized.uploadedBy()).isEqualTo("admin@rijvia.be");
        assertThat(normalized.contentType()).isEqualTo("image/jpeg");
    }

    @Test
    void rejectsMissingLocalizedAltText() {
        var incomplete = new EditorialArticleImageDtos.UploadMetadata(
                "rijvia-road",
                "تقاطع طريق في بلجيكا",
                "Een kruispunt in België",
                "Un carrefour routier en Belgique",
                " ");

        assertThatThrownBy(() -> policy.normalize(file("image/jpeg"), incomplete, "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EN");
    }

    @Test
    void rejectsSpoofedOrOversizedUploadContractsBeforeProcessing() {
        assertThatThrownBy(() -> policy.normalize(
                file("image/gif"),
                metadata(),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("JPEG and PNG");

        var oversized = new MockMultipartFile(
                "file", "road.jpg", "image/jpeg", new byte[5 * 1024 * 1024 + 1]);
        assertThatThrownBy(() -> policy.normalize(
                oversized,
                metadata(),
                "admin"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("5 MB");
    }

    private static MockMultipartFile file(String contentType) {
        return new MockMultipartFile("file", "belgian-road.jpg", contentType, new byte[] {1, 2, 3});
    }

    private static EditorialArticleImageDtos.UploadMetadata metadata() {
        return new EditorialArticleImageDtos.UploadMetadata(
                "RijVia EN Belgian road hero.JPG",
                "تقاطع طريق في بلجيكا",
                "Een kruispunt in België",
                "Un carrefour routier en Belgique",
                "A road junction in Belgium");
    }
}
