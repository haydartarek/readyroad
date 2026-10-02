package com.readyroad.readyroadbackend.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@EnabledIfEnvironmentVariable(named = "RUN_S3_INTEGRATION", matches = "true")
class S3MediaStorageIntegrationTest {

    private static final String ENV_FILE = ".env";
    private static S3Client client;
    private static S3MediaStorageService storage;
    private static MediaStorageProperties properties;

    @BeforeAll
    static void setUp() throws IOException {
        Map<String, String> env = loadEnv(Path.of(ENV_FILE));
        properties = new MediaStorageProperties();
        properties.setProvider(env.get("STORAGE_PROVIDER"));
        properties.setEndpoint(env.get("STORAGE_ENDPOINT"));
        properties.setRegion(env.get("STORAGE_REGION"));
        properties.setAccessKey(env.get("STORAGE_ACCESS_KEY"));
        properties.setSecretKey(env.get("STORAGE_SECRET_KEY"));
        properties.setPublicBucket(env.get("STORAGE_PUBLIC_BUCKET"));
        properties.setPrivateBucket(env.get("STORAGE_PRIVATE_BUCKET"));
        properties.setPublicBaseUrl(env.get("STORAGE_PUBLIC_BASE_URL"));
        properties.setPathStyleAccess(Boolean.parseBoolean(env.get("STORAGE_PATH_STYLE_ACCESS")));
        properties.validateForS3();
        client = new MediaStorageConfiguration().mediaS3Client(properties);
        storage = new S3MediaStorageService(client, properties);
    }

    @AfterAll
    static void tearDown() {
        if (client != null) {
            client.close();
        }
    }

    @Test
    void exercisesTemporaryPublicAndPrivateObjectsAndCleansThem() throws Exception {
        String suffix = UUID.randomUUID().toString();
        String publicKey = "_smoke/" + suffix + ".png";
        String privateKey = "_smoke/" + suffix + ".bin";
        byte[] publicBytes = new byte[] {(byte) 0x89, 0x50, 0x4e, 0x47};
        byte[] privateBytes = "private smoke object".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        try {
            storage.put(MediaStorageBucket.PUBLIC, publicKey,
                    new ByteArrayInputStream(publicBytes), publicBytes.length, "image/png");
            assertThat(storage.exists(MediaStorageBucket.PUBLIC, publicKey)).isTrue();
            try (var input = storage.open(MediaStorageBucket.PUBLIC, publicKey)) {
                assertThat(input.readAllBytes()).isEqualTo(publicBytes);
            }
            assertThat(storage.resolveUrl(MediaStorageBucket.PUBLIC, publicKey))
                    .isEqualTo(properties.getPublicBaseUrl() + "/_smoke/" + suffix + ".png");

            storage.put(MediaStorageBucket.PRIVATE, privateKey,
                    new ByteArrayInputStream(privateBytes), privateBytes.length,
                    "image/png");
            assertThat(storage.exists(MediaStorageBucket.PRIVATE, privateKey)).isTrue();
            try (var input = storage.open(MediaStorageBucket.PRIVATE, privateKey)) {
                assertThat(input.readAllBytes()).isEqualTo(privateBytes);
            }
            assertThatThrownBy(() -> storage.resolveUrl(MediaStorageBucket.PRIVATE, privateKey))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Private media");
        } finally {
            storage.delete(MediaStorageBucket.PUBLIC, publicKey);
            storage.delete(MediaStorageBucket.PRIVATE, privateKey);
        }

        assertThat(storage.exists(MediaStorageBucket.PUBLIC, publicKey)).isFalse();
        assertThat(storage.exists(MediaStorageBucket.PRIVATE, privateKey)).isFalse();
        assertThat(count(properties.getPublicBucket())).isEqualTo(762);
        assertThat(count(properties.getPrivateBucket())).isEqualTo(8);
    }

    private static long count(String bucket) {
        return client.listObjectsV2Paginator(ListObjectsV2Request.builder()
                        .bucket(bucket)
                        .build())
                .stream()
                .flatMap(response -> response.contents().stream())
                .count();
    }

    private static Map<String, String> loadEnv(Path path) throws IOException {
        Map<String, String> values = new HashMap<>();
        for (String line : Files.readAllLines(path)) {
            if (line.isBlank() || line.startsWith("#") || !line.contains("=")) {
                continue;
            }
            int separator = line.indexOf('=');
            values.put(line.substring(0, separator), line.substring(separator + 1));
        }
        return values;
    }
}
