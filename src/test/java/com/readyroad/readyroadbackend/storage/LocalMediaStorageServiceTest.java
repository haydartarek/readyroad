package com.readyroad.readyroadbackend.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LocalMediaStorageServiceTest {

    @TempDir
    Path tempDirectory;

    private LocalMediaStorageService service;

    @BeforeEach
    void setUp() {
        MediaStorageProperties properties = new MediaStorageProperties();
        properties.setLocalRootDirectory(tempDirectory.resolve("public").toString());
        properties.setLocalPublicDirectories(Map.of(
                "articles", tempDirectory.resolve("optimized").toString()));
        service = new LocalMediaStorageService(properties);
    }

    @Test
    void storesReadsAndDeletesPublicObject() throws Exception {
        byte[] content = "public image".getBytes(StandardCharsets.UTF_8);

        service.put(MediaStorageBucket.PUBLIC, "quiz/public.png",
                new java.io.ByteArrayInputStream(content), content.length, "image/png");

        assertThat(service.exists(MediaStorageBucket.PUBLIC, "quiz/public.png")).isTrue();
        assertThat(Files.exists(tempDirectory.resolve("public/quiz/public.png"))).isTrue();
        try (var input = service.open(MediaStorageBucket.PUBLIC, "quiz/public.png")) {
            assertThat(input.readAllBytes()).isEqualTo(content);
        }
        assertThat(service.resolveUrl(MediaStorageBucket.PUBLIC, "quiz/public.png"))
                .isEqualTo("/images/quiz/public.png");
        assertThat(service.delete(MediaStorageBucket.PUBLIC, "quiz/public.png")).isTrue();
        assertThat(service.exists(MediaStorageBucket.PUBLIC, "quiz/public.png")).isFalse();
    }

    @Test
    void preservesLessonPublicLayout() throws Exception {
        byte[] content = "lesson image".getBytes(StandardCharsets.UTF_8);

        service.put(MediaStorageBucket.PUBLIC, "lessons/les-30/page-1.png",
                new java.io.ByteArrayInputStream(content), content.length, "image/png");

        assertThat(Files.exists(tempDirectory.resolve("public/lessons/les-30/page-1.png"))).isTrue();
        assertThat(service.resolveUrl(MediaStorageBucket.PUBLIC, "lessons/les-30/page-1.png"))
                .isEqualTo("/images/lessons/les-30/page-1.png");
    }

    @Test
    void storesReadsAndDeletesPrivateObjectWithoutPublicUrl() throws Exception {
        byte[] content = "private image".getBytes(StandardCharsets.UTF_8);

        service.put(MediaStorageBucket.PRIVATE, "media/private.png",
                new java.io.ByteArrayInputStream(content), content.length, "image/png");

        assertThat(service.exists(MediaStorageBucket.PRIVATE, "media/private.png")).isTrue();
        assertThat(Files.exists(tempDirectory.resolve("private-media/media/private.png"))).isTrue();
        try (var input = service.open(MediaStorageBucket.PRIVATE, "media/private.png")) {
            assertThat(input.readAllBytes()).isEqualTo(content);
        }
        assertThatThrownBy(() -> service.resolveUrl(MediaStorageBucket.PRIVATE, "media/private.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Private media");
        assertThat(service.delete(MediaStorageBucket.PRIVATE, "media/private.png")).isTrue();
        assertThat(service.exists(MediaStorageBucket.PRIVATE, "media/private.png")).isFalse();
    }

    @Test
    void storesArticlePublicObjectsInConfiguredEditorialDirectory() throws Exception {
        byte[] content = "article image".getBytes(StandardCharsets.UTF_8);

        service.put(MediaStorageBucket.PUBLIC, "articles/article-key/hero.jpg",
                new java.io.ByteArrayInputStream(content), content.length, "image/jpeg");

        assertThat(Files.exists(tempDirectory.resolve("optimized/article-key/hero.jpg"))).isTrue();
        assertThat(Files.exists(tempDirectory.resolve("article-key/hero.jpg"))).isFalse();
        assertThat(service.resolveUrl(MediaStorageBucket.PUBLIC, "articles/article-key/hero.jpg"))
                .isEqualTo("/images/articles/article-key/hero.jpg");
    }

    @Test
    void rejectsTraversalKeysBeforeTouchingDisk() {
        assertThatThrownBy(() -> service.exists(MediaStorageBucket.PUBLIC, "media/../secret.png"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThat(Files.exists(tempDirectory.resolve("secret.png"))).isFalse();
    }

    @Test
    void rejectsBackslashAndAbsoluteKeys() {
        assertThatThrownBy(() -> service.exists(MediaStorageBucket.PUBLIC, "media\\file.png"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.exists(MediaStorageBucket.PUBLIC, "/media/file.png"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.exists(MediaStorageBucket.PUBLIC, ""))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
