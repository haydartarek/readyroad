package com.readyroad.readyroadbackend.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.readyroad.readyroadbackend.domain.entity.SignExamResult;
import com.readyroad.readyroadbackend.domain.repository.SignExamResultRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

/** Regression coverage for legacy TEXT values in sign-exam history. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class SignExamResultTextPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", POSTGRES::getJdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("readyroad.marketing.enabled", () -> "false");
        registry.add("jwt.secret-key", () -> "cGhhc2UtdGV4dC1vbmx5LWp3dC1zZWNyZXQtbm90LWZvci1wcm9kdWN0aW9u");
        registry.add("readyroad.admin.default-password", () -> "Text-Test-Only-2026!");
    }

    @Autowired
    JdbcTemplate jdbc;

    @Autowired
    SignExamResultRepository repository;

    @Test
    void readsLegacyTextPayloadWithoutTryingToOpenAPostgresLargeObject() {
        String suffix = UUID.randomUUID().toString().replace("-", "");
        long userId = jdbc.queryForObject("""
                INSERT INTO users (username, email, full_name, password_hash, role)
                VALUES (?, ?, 'LOB regression user', 'not-a-real-secret', 'USER')
                RETURNING id
                """, Long.class, "lob-user-" + suffix, "lob-" + suffix + "@test.local");
        long signId = jdbc.queryForObject("""
                INSERT INTO road_signs
                    (sign_code, normalized_sign_code, category, image_path)
                VALUES (?, ?, 'DANGER', '')
                RETURNING id
                """, Long.class, "LOB-" + suffix, "lob-" + suffix);

        jdbc.update("""
                INSERT INTO sign_exam_results
                    (user_id, sign_id, sign_code, total_questions, answered_count,
                     correct_count, required_to_pass, score_pct, passed,
                     question_results_json, completed_at, created_at)
                VALUES (?, ?, ?, 1, 1, 0, 1, 0, false, ?, ?, ?)
                """, userId, signId, "LOB-" + suffix, "46904",
                LocalDateTime.now(), LocalDateTime.now());

        List<SignExamResult> results = repository.findByUserIdOrderByCompletedAtDesc(userId);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().getQuestionResultsJson()).isEqualTo("46904");
    }
}
