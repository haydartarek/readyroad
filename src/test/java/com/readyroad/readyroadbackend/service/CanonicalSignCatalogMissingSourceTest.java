package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CanonicalSignCatalogMissingSourceTest {

    @TempDir
    Path temporaryCatalog;

    @Test
    void missingCanonicalSignFailsWithoutReplacingTheLastValidCatalog() throws IOException {
        CanonicalSignCatalogService service = new CanonicalSignCatalogService(
                new ObjectMapper(), new DefaultResourceLoader());
        Path source = Path.of("src/main/resources/data/signs_import").toAbsolutePath().normalize();
        ReflectionTestUtils.setField(service, "signsImportPath", source.toString());
        service.refresh();
        assertThat(service.getCanonicalSeeds()).hasSize(184);

        try (Stream<Path> directories = Files.list(source)) {
            for (Path directory : directories.filter(Files::isDirectory).toList()) {
                if (directory.getFileName().toString().equals("A15")) {
                    continue;
                }
                Path targetDirectory = temporaryCatalog.resolve(directory.getFileName().toString());
                Files.createDirectories(targetDirectory);
                Files.copy(directory.resolve("sign.json"), targetDirectory.resolve("sign.json"));
            }
        }

        ReflectionTestUtils.setField(service, "signsImportPath", temporaryCatalog.toString());

        assertThatThrownBy(service::refresh)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("exactly 184")
                .hasMessageContaining("found 183");
        assertThat(service.getCanonicalSeeds()).hasSize(184);
    }
}
