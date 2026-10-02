package com.readyroad.readyroadbackend.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.S3Client;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class S3MediaStorageServiceTest {

    @Mock
    S3Client client;

    private S3MediaStorageService service;

    @BeforeEach
    void setUp() {
        MediaStorageProperties properties = new MediaStorageProperties();
        properties.setPublicBucket("rijvia-public");
        properties.setPrivateBucket("rijvia-private");
        properties.setPublicBaseUrl("https://cdn.example.test/media");
        service = new S3MediaStorageService(client, properties);
    }

    @Test
    void writesToConfiguredBucketAndKey() throws Exception {
        byte[] content = "object".getBytes(StandardCharsets.UTF_8);

        service.put(MediaStorageBucket.PUBLIC, "media/public.png",
                new java.io.ByteArrayInputStream(content), content.length, "image/png");

        ArgumentCaptor<PutObjectRequest> request = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(client).putObject(request.capture(), any(RequestBody.class));
        assertThat(request.getValue().bucket()).isEqualTo("rijvia-public");
        assertThat(request.getValue().key()).isEqualTo("media/public.png");
        assertThat(service.resolveUrl(MediaStorageBucket.PUBLIC, "media/public.png"))
                .isEqualTo("https://cdn.example.test/media/media/public.png");
    }

    @Test
    void privateOperationsUsePrivateBucketAndCannotResolveUrl() throws Exception {
        assertThat(service.delete(MediaStorageBucket.PRIVATE, "media/private.png")).isTrue();
        ArgumentCaptor<DeleteObjectRequest> request = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(client).deleteObject(request.capture());
        assertThat(request.getValue().bucket()).isEqualTo("rijvia-private");
        assertThatThrownBy(() -> service.resolveUrl(MediaStorageBucket.PRIVATE, "media/private.png"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Private media");
    }

    @Test
    void validatesS3SettingsWithClearEnvironmentNames() {
        MediaStorageProperties properties = new MediaStorageProperties();
        properties.setProvider("s3");

        assertThatThrownBy(properties::validateForS3)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("STORAGE_ENDPOINT")
                .hasMessageContaining("STORAGE_ACCESS_KEY")
                .hasMessageContaining("STORAGE_PUBLIC_BASE_URL");
    }

    @Test
    void buildsS3ClientFromEndpointRegionAndPathStyleSettings() {
        MediaStorageProperties properties = new MediaStorageProperties();
        properties.setProvider("s3");
        properties.setEndpoint("https://storage.example.test/s3");
        properties.setRegion("eu-west-1");
        properties.setAccessKey("test-access");
        properties.setSecretKey("test-secret");
        properties.setPublicBucket("rijvia-public");
        properties.setPrivateBucket("rijvia-private");
        properties.setPublicBaseUrl("https://cdn.example.test");
        properties.setPathStyleAccess(true);

        S3Client client = new MediaStorageConfiguration().mediaS3Client(properties);
        assertThat(client).isNotNull();
        assertThat(client.serviceClientConfiguration().region().id()).isEqualTo("eu-west-1");
        assertThat(client.serviceClientConfiguration().endpointOverride())
                .hasValue(java.net.URI.create("https://storage.example.test/s3"));
        assertThat(properties.isPathStyleAccess()).isTrue();
        client.close();
    }

    @Test
    void localModeDoesNotCreateS3ClientBean() {
        new ApplicationContextRunner()
                .withUserConfiguration(
                        MediaStorageConfiguration.class,
                        LocalMediaStorageService.class,
                        S3MediaStorageService.class)
                .withPropertyValues("app.media.storage.provider=local")
                .run(context -> assertThat(context).doesNotHaveBean(S3Client.class));
    }
}
