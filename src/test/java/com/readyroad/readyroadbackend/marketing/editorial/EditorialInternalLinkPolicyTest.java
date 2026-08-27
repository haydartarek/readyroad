package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class EditorialInternalLinkPolicyTest {

    private final EditorialInternalLinkStore store = mock(EditorialInternalLinkStore.class);
    private final EditorialInternalLinkPolicy policy = new EditorialInternalLinkPolicy(store);

    @Test
    void resolvesPublishedArticlesAndRealLearningAssets() {
        when(store.publishedArticleId("AR", "published-article")).thenReturn(Optional.of(22L));
        when(store.lessonExists("les-19", 2)).thenReturn(true);
        when(store.trafficSignExists("A1")).thenReturn(true);
        when(store.practiceCategoryExists("A")).thenReturn(true);

        var links = policy.normalize(10L, "AR", List.of(
                input("/ar/blog/published-article", "مقال مرتبط"),
                input("/ar/lessons/les-19/2", "درس الأولوية"),
                input("/ar/traffic-signs/A1", "علامة الخطر"),
                input("/ar/practice/A", "تدرب على الفئة A"),
                input("/ar/exam", "ابدأ محاكي الامتحان"),
                input("/ar/videos", "شاهد فيديوهات القيادة")));

        assertThat(links)
                .extracting(EditorialInternalLinkDtos.Link::type)
                .containsExactly("ARTICLE", "LESSON", "TRAFFIC_SIGN", "PRACTICE", "EXAM", "VIDEO");
    }

    @Test
    void normalizesSameDomainHttpsUrlsToLocalizedInternalPaths() {
        var links = policy.normalize(10L, "AR", List.of(
                input("https://rijvia.be/ar/exam", "ابدأ محاكي الامتحان"),
                input("https://www.rijvia.be/ar/videos/", "شاهد فيديوهات القيادة")));

        assertThat(links)
                .extracting(EditorialInternalLinkDtos.Link::targetPath)
                .containsExactly("/ar/exam", "/ar/videos");
    }

    @Test
    void rejectsCrossLanguageDuplicateAndGenericLinks() {
        assertThatThrownBy(() -> policy.normalize(10L, "AR", List.of(
                input("/fr/exam", "ابدأ الامتحان"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("language");

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("/exam", "Official exam"),
                input("/exam/", "Try the exam"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Duplicate");

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("/exam", "Click here"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("describe its destination");
    }

    @Test
    void rejectsUnpublishedAndSelfArticleTargets() {
        when(store.publishedArticleId("EN", "draft-only")).thenReturn(Optional.empty());
        when(store.publishedArticleId("EN", "same-article")).thenReturn(Optional.of(10L));

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("/blog/draft-only", "Draft article"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not published");

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("/blog/same-article", "Same article"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("cannot link to itself");
    }

    @Test
    void rejectsUnknownAssetsAndExternalUrls() {
        when(store.lessonExists("missing", null)).thenReturn(false);

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("/lessons/missing", "Missing lesson"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown lesson");

        assertThatThrownBy(() -> policy.normalize(10L, "EN", List.of(
                input("https://example.com", "External source"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Only HTTPS RijVia URLs");
    }

    private static EditorialInternalLinkDtos.Input input(String targetPath, String anchorText) {
        return new EditorialInternalLinkDtos.Input(targetPath, anchorText);
    }
}
