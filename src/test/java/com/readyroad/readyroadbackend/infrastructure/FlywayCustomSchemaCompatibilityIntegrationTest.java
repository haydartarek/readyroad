package com.readyroad.readyroadbackend.infrastructure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Testcontainers
class FlywayCustomSchemaCompatibilityIntegrationTest {

    private static final String APP_USER = "readyroad_app";
    private static final String APP_PASSWORD = "ReadyRoad-Test-Only-2026!";
    private static final String APP_SCHEMA = "readyroad";

    @Container
    static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17-alpine");

    @Test
    void migratesHistoricalV59SafelyOnRestrictedCustomSchema() throws Exception {
        prepareRestrictedApplicationRole();
        String appJdbcUrl = withCurrentSchema(POSTGRES.getJdbcUrl(), APP_SCHEMA);

        flyway(appJdbcUrl, "58").migrate();

        try (Connection app = appConnection(appJdbcUrl)) {
            assertThat(queryBoolean(
                    app,
                    "SELECT has_schema_privilege(current_user, 'public', 'CREATE')"))
                    .isFalse();
            assertThat(queryString(app, """
                    SELECT n.nspname
                    FROM pg_proc p
                    JOIN pg_namespace n ON n.oid = p.pronamespace
                    WHERE p.proname = 'protect_article_version_history'
                    """))
                    .isEqualTo(APP_SCHEMA);
        }

        grantTemporaryPublicCreate();
        flyway(appJdbcUrl, null).migrate();
        revokeTemporaryPublicCreate();

        try (Connection app = appConnection(appJdbcUrl)) {
            assertThat(queryInt(app, """
                    SELECT checksum
                    FROM flyway_schema_history
                    WHERE version = '59'
                    """))
                    .isEqualTo(-1541034669);
            assertThat(queryString(app, """
                    SELECT string_agg(version, ',' ORDER BY installed_rank)
                    FROM flyway_schema_history
                    WHERE version IN ('59', '60', '61', '62')
                    """))
                    .isEqualTo("59,60,61,62");
            assertThat(queryBoolean(
                    app,
                    "SELECT has_schema_privilege(current_user, 'public', 'CREATE')"))
                    .isFalse();
            assertThat(queryBoolean(
                    app,
                    "SELECT to_regprocedure('public.protect_article_version_history()') IS NULL"))
                    .isTrue();
            assertThat(queryString(app, """
                    SELECT pg_get_functiondef('readyroad.protect_article_version_history()'::regprocedure)
                    """))
                    .contains("rijvia.allow_article_version_delete");
            assertThat(queryString(app, """
                    SELECT fn.nspname
                    FROM pg_trigger t
                    JOIN pg_class c ON c.oid = t.tgrelid
                    JOIN pg_proc p ON p.oid = t.tgfoid
                    JOIN pg_namespace fn ON fn.oid = p.pronamespace
                    WHERE NOT t.tgisinternal
                      AND c.relname = 'article_versions'
                      AND t.tgname = 'trg_protect_article_version_history'
                    """))
                    .isEqualTo(APP_SCHEMA);

            long versionId = insertVersionFixture(app);
            SQLException blockedDelete = assertThrows(SQLException.class, () -> deleteVersion(app, versionId));
            assertThat(blockedDelete.getSQLState()).isEqualTo("23503");
            assertThat(countVersion(app, versionId)).isOne();

            app.setAutoCommit(false);
            try {
                try (Statement statement = app.createStatement()) {
                    statement.execute("SET LOCAL rijvia.allow_article_version_delete = 'on'");
                }
                assertThat(deleteVersion(app, versionId)).isOne();
                app.commit();
            } catch (Exception exception) {
                app.rollback();
                throw exception;
            } finally {
                app.setAutoCommit(true);
            }
            assertThat(countVersion(app, versionId)).isZero();
        }
    }

    private static Flyway flyway(String appJdbcUrl, String target) {
        FluentConfiguration configuration = Flyway.configure()
                .dataSource(appJdbcUrl, APP_USER, APP_PASSWORD)
                .locations("classpath:db/migration-postgresql")
                .schemas(APP_SCHEMA)
                .defaultSchema(APP_SCHEMA)
                .createSchemas(false)
                .validateOnMigrate(true);
        if (target != null) {
            configuration.target(MigrationVersion.fromVersion(target));
        }
        return configuration.load();
    }

    private static void prepareRestrictedApplicationRole() throws SQLException {
        try (Connection admin = adminConnection(); Statement statement = admin.createStatement()) {
            statement.execute("CREATE ROLE readyroad_app LOGIN PASSWORD 'ReadyRoad-Test-Only-2026!'");
            statement.execute("REVOKE CREATE ON SCHEMA public FROM PUBLIC");
            statement.execute("CREATE SCHEMA readyroad AUTHORIZATION readyroad_app");
            statement.execute("GRANT USAGE ON SCHEMA public TO readyroad_app");
        }
    }

    private static void grantTemporaryPublicCreate() throws SQLException {
        try (Connection admin = adminConnection(); Statement statement = admin.createStatement()) {
            statement.execute("GRANT CREATE ON SCHEMA public TO readyroad_app");
        }
    }

    private static void revokeTemporaryPublicCreate() throws SQLException {
        try (Connection admin = adminConnection(); Statement statement = admin.createStatement()) {
            statement.execute("REVOKE CREATE ON SCHEMA public FROM readyroad_app");
        }
    }

    private static long insertVersionFixture(Connection app) throws SQLException {
        long topicId;
        try (Statement statement = app.createStatement();
             ResultSet result = statement.executeQuery("SELECT id FROM article_topics ORDER BY id LIMIT 1")) {
            assertThat(result.next()).isTrue();
            topicId = result.getLong(1);
        }

        long articleId;
        try (PreparedStatement statement = app.prepareStatement("""
                INSERT INTO articles (
                    article_topic_id, canonical_key, lifecycle_state, canonical_language
                ) VALUES (?, 'custom-schema-compatibility', 'IDEA', 'EN')
                RETURNING id
                """)) {
            statement.setLong(1, topicId);
            try (ResultSet result = statement.executeQuery()) {
                assertThat(result.next()).isTrue();
                articleId = result.getLong(1);
            }
        }

        try (PreparedStatement statement = app.prepareStatement("""
                INSERT INTO article_versions (
                    article_id, version_number, language, title, slug, summary, body,
                    status, is_current, created_by
                ) VALUES (?, 1, 'EN', 'Compatibility test', 'compatibility-test',
                          'Compatibility summary', 'Compatibility body', 'DRAFT', TRUE, 'test')
                RETURNING id
                """)) {
            statement.setLong(1, articleId);
            try (ResultSet result = statement.executeQuery()) {
                assertThat(result.next()).isTrue();
                return result.getLong(1);
            }
        }
    }

    private static int deleteVersion(Connection app, long versionId) throws SQLException {
        try (PreparedStatement statement = app.prepareStatement("DELETE FROM article_versions WHERE id = ?")) {
            statement.setLong(1, versionId);
            return statement.executeUpdate();
        }
    }

    private static int countVersion(Connection app, long versionId) throws SQLException {
        try (PreparedStatement statement = app.prepareStatement(
                "SELECT count(*) FROM article_versions WHERE id = ?")) {
            statement.setLong(1, versionId);
            try (ResultSet result = statement.executeQuery()) {
                assertThat(result.next()).isTrue();
                return result.getInt(1);
            }
        }
    }

    private static boolean queryBoolean(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            assertThat(result.next()).isTrue();
            return result.getBoolean(1);
        }
    }

    private static int queryInt(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            assertThat(result.next()).isTrue();
            return result.getInt(1);
        }
    }

    private static String queryString(Connection connection, String sql) throws SQLException {
        try (Statement statement = connection.createStatement(); ResultSet result = statement.executeQuery(sql)) {
            assertThat(result.next()).isTrue();
            return result.getString(1);
        }
    }

    private static Connection adminConnection() throws SQLException {
        return DriverManager.getConnection(POSTGRES.getJdbcUrl(), POSTGRES.getUsername(), POSTGRES.getPassword());
    }

    private static Connection appConnection(String appJdbcUrl) throws SQLException {
        return DriverManager.getConnection(appJdbcUrl, APP_USER, APP_PASSWORD);
    }

    private static String withCurrentSchema(String jdbcUrl, String schema) {
        return jdbcUrl + (jdbcUrl.contains("?") ? "&" : "?") + "currentSchema=" + schema;
    }
}
