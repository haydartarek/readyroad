package com.readyroad.readyroadbackend.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
class LessonCategoryReleaseMigrationPostgreSqlIntegrationTest {

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Test
    void upgradesV77WithoutReplacingLessonsOrExistingAssociations() throws Exception {
        flyway("77").migrate();
        try (Connection connection = connection(); Statement statement = connection.createStatement()) {
            statement.execute("""
                    -- Production V77 already contains these imported catalogue categories.
                    INSERT INTO categories (code, name_en, name_nl, name_fr, name_ar,
                                            content_scope, created_at, updated_at)
                    VALUES ('TH09', 'Motorway', 'Motorway', 'Motorway', 'Motorway',
                            'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
                           ('TH10', 'Vehicle technology', 'Vehicle technology', 'Vehicle technology',
                            'Vehicle technology', 'THEORETICAL_EXAM', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
                    ON CONFLICT (code) DO NOTHING;
                    INSERT INTO lessons (lesson_code, title_en, title_nl, title_fr, title_ar)
                    SELECT 'les-' || n, 'Existing content', 'Existing content',
                           'Existing content', 'Existing content'
                    FROM generate_series(0, 31) AS lesson(n);
                    INSERT INTO lessons (lesson_code, title_en, title_nl, title_fr, title_ar)
                    VALUES ('additional-production-lesson', 'Keep', 'Keep', 'Keep', 'Keep');
                    INSERT INTO lesson_categories (lesson_id, category_id, is_primary, display_order)
                    SELECT l.id, c.id, TRUE, 7 FROM lessons l CROSS JOIN categories c
                    WHERE l.lesson_code = 'additional-production-lesson' AND c.code = 'TH01';
                    """);
        }

        assertThat(flyway("78").migrate().migrationsExecuted).isEqualTo(1);
        try (Connection connection = connection(); Statement statement = connection.createStatement()) {
            assertThat(count(statement, "SELECT count(*) FROM lesson_categories")).isEqualTo(33);
            assertThat(count(statement, """
                    SELECT count(*) FROM lessons l JOIN lesson_categories lc ON lc.lesson_id = l.id
                    WHERE l.lesson_code ~ '^les-[0-9]+$' AND lc.is_primary AND lc.display_order = 1
                    """)).isEqualTo(32);
            assertThat(count(statement,
                    "SELECT count(*) FROM lessons WHERE title_en = 'Existing content'")).isEqualTo(32);
            assertThat(count(statement,
                    "SELECT count(*) FROM lesson_categories WHERE display_order = 7")).isEqualTo(1);

            // Reapplying the data statement changes no existing row or timestamp.
            String migration = new ClassPathResource(
                    "db/migration-postgresql/V78__Seed_Approved_Lesson_Theory_Categories.sql")
                    .getContentAsString(StandardCharsets.UTF_8);
            assertThat(statement.executeUpdate(migration)).isZero();
            assertThat(count(statement, "SELECT count(*) FROM lesson_categories")).isEqualTo(33);
        }
        assertThat(flyway("78").migrate().migrationsExecuted).isZero();
    }

    private static Flyway flyway(String target) {
        return Flyway.configure().dataSource(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(),
                POSTGRES.getPassword()).schemas("readyroad").defaultSchema("readyroad")
                .locations("classpath:db/migration-postgresql").target(target).load();
    }

    private static Connection connection() throws Exception {
        Connection connection = DriverManager.getConnection(POSTGRES.getJdbcUrl(),
                POSTGRES.getUsername(), POSTGRES.getPassword());
        connection.setSchema("readyroad");
        return connection;
    }

    private static long count(Statement statement, String sql) throws Exception {
        try (ResultSet result = statement.executeQuery(sql)) {
            result.next();
            return result.getLong(1);
        }
    }
}
