package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.storage.MediaStorageBucket;
import com.readyroad.readyroadbackend.storage.MediaStorageService;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Mock BackendMessageService messages;
    @Mock MediaStorageService mediaStorageService;
    private FileUploadService service;

    @BeforeEach
    void setUp() {
        lenient().when(messages.get(anyString(), any(Object[].class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        service = new FileUploadService(messages, mediaStorageService);
        ReflectionTestUtils.setField(service, "maxFileSizeMb", 5);
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
        verify(mediaStorageService).put(
                org.mockito.ArgumentMatchers.eq(MediaStorageBucket.PUBLIC),
                org.mockito.ArgumentMatchers.matches("quiz/[a-f0-9-]+\\.png"),
                any(InputStream.class),
                org.mockito.ArgumentMatchers.eq((long) output.size()),
                org.mockito.ArgumentMatchers.eq("image/png"));
    }

    @Test
    void rejectsFakeImageBytesWithoutWritingAFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "broken.png", "image/png", "not-an-image".getBytes());

        assertThatThrownBy(() -> service.uploadImage(file))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("upload.unreadable_image");
        verifyNoInteractions(mediaStorageService);
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
        verify(mediaStorageService).put(
                org.mockito.ArgumentMatchers.eq(MediaStorageBucket.PUBLIC),
                org.mockito.ArgumentMatchers.matches("quiz/priority-signs-2026-[a-f0-9-]{12}\\.png"),
                any(InputStream.class),
                org.mockito.ArgumentMatchers.eq((long) output.size()),
                org.mockito.ArgumentMatchers.eq("image/png"));
    }

    @Test
    void sanitizesUnicodeNamesWithoutAllowingPathTraversal() {
        assertThat(FileUploadService.sanitizeBaseName("../علامات الأولوية\\ سؤال"))
                .isEqualTo("علامات-الأولوية-سؤال");
    }

    @Test
    void permanentlyDeletesLessonImageFromLessonStorage() throws Exception {
        when(mediaStorageService.delete(MediaStorageBucket.PUBLIC, "lessons/TH01/page-1.png"))
                .thenReturn(true, false);

        assertThat(service.deleteLessonImage("lessons/TH01/page-1.png")).isTrue();
        assertThat(service.deleteLessonImage("lessons/TH01/page-1.png")).isFalse();
        verify(mediaStorageService, org.mockito.Mockito.times(2))
                .delete(MediaStorageBucket.PUBLIC, "lessons/TH01/page-1.png");
    }

    @Test
    void rejectsLessonStoragePathTraversal() {
        assertThatThrownBy(() -> service.deleteLessonImage("lessons/../quiz/image.png"))
                .isInstanceOf(SecurityException.class);
    }

    @Test
    void rejectsMaliciousLessonCodeBeforeStorage() throws Exception {
        assertThatThrownBy(() -> service.uploadLessonImage(
                new MockMultipartFile("file", "lesson.png", "image/png", validPngBytes()),
                "../les-X",
                null))
                .isInstanceOf(SecurityException.class);
        verifyNoInteractions(mediaStorageService);
    }

    @Test
    void rejectsQuizDeleteTraversalBeforeStorage() {
        assertThat(service.deleteImage("/images/quiz/../private.png")).isFalse();
        verifyNoInteractions(mediaStorageService);
    }

    @Test
    void validLessonUploadUsesPublicLessonKeyAndKeepsUrlContract() throws Exception {
        byte[] bytes = validPngBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file", "lesson.png", "image/png", bytes);

        String reference = service.uploadLessonImage(file, "les-X", null);

        assertThat(reference).matches("^/images/lessons/les-x/[a-f0-9-]+\\.png$");
        verify(mediaStorageService).put(
                org.mockito.ArgumentMatchers.eq(MediaStorageBucket.PUBLIC),
                org.mockito.ArgumentMatchers.matches("lessons/les-x/[a-f0-9-]+\\.png"),
                any(InputStream.class),
                org.mockito.ArgumentMatchers.eq((long) bytes.length),
                org.mockito.ArgumentMatchers.eq("image/png"));
    }

    @Test
    void oversizedInvalidMimeAndExtensionDoNotCallStorage() {
        MockMultipartFile badMime = new MockMultipartFile(
                "file", "x.png", "text/plain", new byte[] {1});
        MockMultipartFile badExtension = new MockMultipartFile(
                "file", "x.gif", "image/png", new byte[] {1});
        MockMultipartFile oversized = new MockMultipartFile(
                "file", "x.png", "image/png", new byte[5 * 1024 * 1024 + 1]);

        assertThatThrownBy(() -> service.uploadImage(badMime)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.uploadImage(badExtension)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.uploadImage(oversized)).isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(mediaStorageService);
    }

    private static byte[] validPngBytes() throws Exception {
        BufferedImage image = new BufferedImage(40, 30, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return output.toByteArray();
    }
}
