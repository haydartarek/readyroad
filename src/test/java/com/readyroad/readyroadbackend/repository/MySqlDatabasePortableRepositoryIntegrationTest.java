package com.readyroad.readyroadbackend.repository;

import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("prod")
@EnabledIfEnvironmentVariable(named = "READYROAD_DATABASE_PORTABILITY_TESTS", matches = "true")
class MySqlDatabasePortableRepositoryIntegrationTest
        extends AbstractDatabasePortableRepositoryIntegrationTest {

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> requiredEnvironment("READYROAD_MYSQL_TEST_URL"));
        registry.add("spring.datasource.username", () -> requiredEnvironment("READYROAD_MYSQL_TEST_USERNAME"));
        registry.add("spring.datasource.password", () -> requiredEnvironment("READYROAD_MYSQL_TEST_PASSWORD"));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("jwt.secret-key",
                () -> "cG9ydGFiaWxpdHktdGVzdC1zZWNyZXQtcG9ydGFiaWxpdHktdGVzdC1zZWNyZXQ=");
        registry.add("readyroad.admin.default-password", () -> "Portability-Test-Only-2026!");
    }

    private static String requiredEnvironment(String name) {
        String value = System.getenv(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(name + " must be configured for external database tests");
        }
        return value;
    }
}
