package com.readyroad.readyroadbackend.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MediaUrlResolverTest {

    private MediaStorageService mediaStorageService;
    private MediaUrlResolver resolver;

    @BeforeEach
    void setUp() {
        mediaStorageService = mock(MediaStorageService.class);
        resolver = new MediaUrlResolver(mediaStorageService);
    }

    @Test
    void resolvesImagesPathThroughPublicStorage() {
        when(mediaStorageService.resolveUrl(MediaStorageBucket.PUBLIC, "quiz/question.png"))
                .thenReturn("https://cdn.example/quiz/question.png");

        assertThat(resolver.resolvePublicUrl("/images/quiz/question.png"))
                .isEqualTo("https://cdn.example/quiz/question.png");
        verify(mediaStorageService).resolveUrl(MediaStorageBucket.PUBLIC, "quiz/question.png");
    }

    @Test
    void preservesAbsoluteAndDataUrls() {
        assertThat(resolver.resolvePublicUrl("https://cdn.example/image.png"))
                .isEqualTo("https://cdn.example/image.png");
        assertThat(resolver.resolvePublicUrl("data:image/png;base64,abc"))
                .isEqualTo("data:image/png;base64,abc");
    }

    @Test
    void rejectsPrivateLogicalPaths() {
        assertThatThrownBy(() -> resolver.resolvePublicUrl("originals/articles/a/original.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Private media");
    }
}
