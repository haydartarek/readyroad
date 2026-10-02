package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.storage.MediaStorageBucket;
import com.readyroad.readyroadbackend.storage.MediaStorageService;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class EditorialArticleImageProcessorTest {

    @Mock
    MediaStorageService storage;

    @Mock
    BackendMessageService messages;

    @Test
    void storesPublicVariantsAndPrivateOriginalAndDeletesExactObjects() throws Exception {
        var processor = new EditorialArticleImageProcessor(storage, messages);

        var processed = processor.process(image(), "rijvia-en-hero", "image/jpeg", 0.5, 0.5);

        ArgumentCaptor<String> privateKey = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> publicKeys = ArgumentCaptor.forClass(String.class);
        verify(storage).put(eq(MediaStorageBucket.PRIVATE), privateKey.capture(),
                any(), anyLong(), eq("image/jpeg"));
        verify(storage, times(5)).put(eq(MediaStorageBucket.PUBLIC), publicKeys.capture(),
                any(), anyLong(), eq("image/jpeg"));
        assertThat(privateKey.getValue())
                .isEqualTo("originals/articles/" + processed.storageKey() + "/original.jpg");
        assertThat(publicKeys.getAllValues()).allMatch(
                key -> key.startsWith("articles/" + processed.storageKey() + "/"));
        assertThat(processed.originalStoragePath()).startsWith("archive/").endsWith("/original.jpg");
        assertThat(processed.variants()).allSatisfy(variant ->
                assertThat(variant.publicPath()).startsWith("/images/articles/"));

        processor.delete(processed);

        verify(storage).delete(eq(MediaStorageBucket.PRIVATE),
                eq(privateKey.getValue()));
        for (String key : publicKeys.getAllValues()) {
            verify(storage).delete(MediaStorageBucket.PUBLIC, key);
        }
    }

    @Test
    void rollsBackPrivateOriginalWhenVariantStorageFails() throws Exception {
        doAnswer(invocation -> {
            if (invocation.getArgument(0) == MediaStorageBucket.PUBLIC) {
                throw new IOException("storage unavailable");
            }
            return null;
        }).when(storage).put(any(), anyString(), any(), anyLong(), anyString());
        var processor = new EditorialArticleImageProcessor(storage, messages);

        assertThatThrownBy(() -> processor.process(image(), "rijvia-en-hero", "image/jpeg", 0.5, 0.5))
                .isInstanceOf(IllegalStateException.class);

        verify(storage).delete(eq(MediaStorageBucket.PRIVATE), startsWith("originals/articles/"));
        verify(storage).delete(eq(MediaStorageBucket.PUBLIC), startsWith("articles/"));
    }

    @Test
    void failureAfterSeveralUploadsCleansEveryAttemptedObjectEvenIfOneDeleteFails() throws Exception {
        List<String> privateKeys = new ArrayList<>();
        List<String> publicKeys = new ArrayList<>();
        doAnswer(invocation -> {
            String key = invocation.getArgument(1);
            if (invocation.getArgument(0) == MediaStorageBucket.PRIVATE) {
                privateKeys.add(key);
            } else {
                publicKeys.add(key);
                if (publicKeys.size() == 3) {
                    throw new IOException("interrupted after partial variant uploads");
                }
            }
            return null;
        }).when(storage).put(any(), anyString(), any(), anyLong(), anyString());
        doAnswer(invocation -> {
            throw new IOException("original cleanup unavailable");
        }).when(storage).delete(eq(MediaStorageBucket.PRIVATE), anyString());
        var processor = new EditorialArticleImageProcessor(storage, messages);

        assertThatThrownBy(() -> processor.process(image(), "rijvia-en-hero", "image/jpeg", 0.5, 0.5))
                .isInstanceOf(IllegalStateException.class);

        assertThat(privateKeys).hasSize(1);
        assertThat(publicKeys).hasSize(3);
        verify(storage).delete(MediaStorageBucket.PRIVATE, privateKeys.getFirst());
        for (String key : publicKeys) {
            verify(storage).delete(MediaStorageBucket.PUBLIC, key);
        }
    }

    @Test
    void rejectsInvalidImageBeforeCreatingAnyStorageObject() throws Exception {
        var processor = new EditorialArticleImageProcessor(storage, messages);
        var invalid = new MockMultipartFile(
                "file", "hero.jpg", "image/jpeg", "not-an-image".getBytes());

        assertThatThrownBy(() -> processor.process(invalid, "rijvia-en-hero", "image/jpeg", 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        org.mockito.Mockito.verifyNoInteractions(storage);
    }

    private static MockMultipartFile image() throws Exception {
        BufferedImage image = new BufferedImage(64, 64, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                image.setRGB(x, y, Color.BLUE.getRGB());
            }
        }
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ImageIO.write(image, "jpeg", bytes);
        return new MockMultipartFile("file", "hero.jpg", "image/jpeg", bytes.toByteArray());
    }
}
