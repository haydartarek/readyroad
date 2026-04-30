package com.readyroad.readyroadbackend.integration;

import com.readyroad.readyroadbackend.ReadyroadApplication;
import com.readyroad.readyroadbackend.domain.entity.SignImportRun;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.domain.repository.SignImportRunRepository;
import com.readyroad.readyroadbackend.domain.repository.SignQuestionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Feature: Sign quiz startup import regression guard")
class SignQuizStartupImportIntegrationTest {

    @Test
    @DisplayName("Clean startup imports sign quiz data using the default module-relative dataset path")
    void startupImportUsesDefaultModuleRelativePath() {
        assertStartupImportSucceeds(null);
    }

    @Test
    @DisplayName("Clean startup imports sign quiz data even when the configured path is invalid")
    void startupImportFallsBackWhenConfiguredPathIsInvalid() {
        assertStartupImportSucceeds("missing/signs_import");
    }

    private void assertStartupImportSucceeds(String configuredImportPath) {
        try (ConfigurableApplicationContext context = startApplication(configuredImportPath)) {
            RoadSignRepository roadSignRepository = context.getBean(RoadSignRepository.class);
            SignQuestionRepository signQuestionRepository = context.getBean(SignQuestionRepository.class);
            SignImportRunRepository signImportRunRepository = context.getBean(SignImportRunRepository.class);

            assertThat(roadSignRepository.countByIsActiveTrue()).isGreaterThan(0);
            assertThat(signQuestionRepository.count()).isGreaterThan(0);
            assertThat(roadSignRepository.findFirstBySignCodeAndIsActiveTrueOrderByIdAsc("A1a")).isPresent();

            SignImportRun lastRun = signImportRunRepository.findTopByOrderByCreatedAtDesc()
                    .orElseThrow(() -> new AssertionError("Expected a startup sign import run"));

            assertThat(lastRun.getStatus()).isEqualTo("SUCCESS");
            assertThat(lastRun.getErrorsCount()).isZero();
            assertThat(lastRun.getSignsProcessed()).isGreaterThan(0);
            assertThat(lastRun.getQuestionsCreated() + lastRun.getQuestionsUpdated()).isGreaterThan(0);
        }
    }

    private ConfigurableApplicationContext startApplication(String configuredImportPath) {
        String databaseName = "sign_quiz_startup_" + UUID.randomUUID().toString().replace("-", "");

        List<String> arguments = new ArrayList<>(List.of(
                "--spring.security.mode=dev",
                "--spring.datasource.url=jdbc:h2:mem:" + databaseName
                        + ";DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=MYSQL",
                "--spring.datasource.username=sa",
                "--spring.datasource.password=",
                "--spring.datasource.driver-class-name=org.h2.Driver",
                "--spring.jpa.hibernate.ddl-auto=create-drop",
                "--spring.jpa.show-sql=false",
                "--spring.jpa.properties.hibernate.format_sql=false",
                "--spring.jpa.defer-datasource-initialization=true",
                "--spring.flyway.enabled=false",
                "--jwt.secret-key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970337336763979244226452948404D6351",
                "--jwt.expiration=3600000",
                "--jwt.refresh-token.expiration=604800000",
                "--jwt.issuer=readyroad-test",
                "--jwt.header=Authorization",
                "--jwt.prefix=Bearer",
                "--readyroad.admin.default-password=test-admin-password",
                "--readyroad.data-import.enabled=false",
                "--spring.jmx.enabled=false",
                "--server.port=0"));

        if (configuredImportPath != null) {
            arguments.add("--readyroad.signs-import.path=" + configuredImportPath);
        }

        return new SpringApplicationBuilder(ReadyroadApplication.class)
                .web(WebApplicationType.NONE)
                .run(arguments.toArray(String[]::new));
    }
}