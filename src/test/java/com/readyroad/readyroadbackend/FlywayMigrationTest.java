package com.readyroad.readyroadbackend;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Database Migration Tests
 *
 * Validates that Flyway migrations apply cleanly
 * and database schema is correctly initialized.
 *
 * NOTE: This test is SKIPPED when Flyway is disabled (test profile uses H2 + Hibernate DDL).
 *       To run this test, use a profile with Flyway enabled (e.g., integration profile with MySQL).
 *
 * @author ReadyRoad Team
 * @since 2026-01-17
 */
@SpringBootTest
@ActiveProfiles("test")
@EnabledIf(
    expression = "#{environment.getProperty('spring.flyway.enabled', 'false') == 'true'}",
    loadContext = true
)
@DisplayName("Database Migration Tests")
public class FlywayMigrationTest {

    @Autowired
    private DataSource dataSource;

    @Autowired(required = false)  // Optional: test is skipped when Flyway is disabled
    private Flyway flyway;

    @Test
    @DisplayName("Flyway migrations should apply successfully")
    public void testFlywayMigrationsApply() {
        // Verify Flyway is configured
        assertThat(flyway).isNotNull();

        // Verify migrations applied
        assertThat(flyway.info().applied()).isNotEmpty();

        // Verify no pending migrations
        assertThat(flyway.info().pending()).isEmpty();
    }

    @Test
    @DisplayName("Database connection should be valid")
    public void testDatabaseConnection() throws Exception {
        assertThat(dataSource).isNotNull();
        assertThat(dataSource.getConnection()).isNotNull();
        assertThat(dataSource.getConnection().isValid(2)).isTrue();
    }

    @Test
    @DisplayName("Schema validation should pass")
    public void testSchemaValidation() {
        // Flyway validate checks if applied migrations match the configured ones
        // This will throw exception if there's a mismatch
        flyway.validate();
    }
}
