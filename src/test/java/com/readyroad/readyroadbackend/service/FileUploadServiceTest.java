package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @TempDir Path tempDir;
    @Mock BackendMessageService messages;
    private FileUploadService service;

    @BeforeEach
    void setUp() {
        lenient().when(messages.get(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        service = new FileUploadService(messages);
        ReflectionTestUtils.setField(service, "maxFileSizeMb", 5);
        ReflectionTestUtils.setField(service, "uploadDirectory", tempDir.toString());
        service.init();
    }

    @Test
    void acceptsAndStoresAReal1920By1080Png() throws Exception {
        BufferedImage image = new BufferedImage(1920, 1080, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        MockMultipartFile file = new MockMultipartFile(
                "file", "question.png", "image/png", output.toByteArray());

        String reference = service.uploadImage(file);

        assertThat(reference).matches("^/images/quiz/[a-f0-9-]+\\.png$");
        assertThat(Files.exists(tempDir.resolve(reference.substring("/images/quiz/".length())))).isTrue();
    }

    @Test
    void rejectsFakeImageBytesWithoutWritingAFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "broken.png", "image/png", "not-an-image".getBytes());

        assertThatThrownBy(() -> service.uploadImage(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("upload.unreadable_image");
        try (var files = Files.list(tempDir)) {
            assertThat(files).isEmpty();
        }
    }

    @Test
    void storesCustomSeoFilenameWithSafeExtensionAndUniqueSuffix() throws Exception {
        BufferedImage image = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        MockMultipartFile file = new MockMultipartFile(
                "file", "camera-name.PNG", "image/png", output.toByteArray());

        String reference = service.uploadImage(file, "  Priority / ../ Signs 2026.png  ");

        assertThat(reference)
                .matches("^/images/quiz/priority-signs-2026-[a-f0-9-]{12}\\.png$");
        assertThat(Files.exists(tempDir.resolve(reference.substring("/images/quiz/".length())))).isTrue();
    }

    @Test
    void sanitizesUnicodeNamesWithoutAllowingPathTraversal() {
        assertThat(FileUploadService.sanitizeBaseName("../علامات الأولوية\\ سؤال"))
                .isEqualTo("علامات-الأولوية-سؤال");
    }
}
