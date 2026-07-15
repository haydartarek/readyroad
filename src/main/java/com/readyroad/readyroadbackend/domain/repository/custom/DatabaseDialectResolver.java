package com.readyroad.readyroadbackend.domain.repository.custom;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;

final class DatabaseDialectResolver {

    enum DatabaseDialect {
        MYSQL("RAND"),
        POSTGRESQL("RANDOM"),
        H2("RAND");

        private final String randomFunction;

        DatabaseDialect(String randomFunction) {
            this.randomFunction = randomFunction;
        }

        String randomFunction() {
            return randomFunction;
        }
    }

    private final DataSource dataSource;
    private volatile DatabaseDialect resolvedDialect;

    DatabaseDialectResolver(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    DatabaseDialect dialect() {
        DatabaseDialect cached = resolvedDialect;
        if (cached != null) {
            return cached;
        }

        synchronized (this) {
            if (resolvedDialect == null) {
                resolvedDialect = detectDialect();
            }
            return resolvedDialect;
        }
    }

    private DatabaseDialect detectDialect() {
        try (Connection connection = dataSource.getConnection()) {
            String productName = connection.getMetaData().getDatabaseProductName().toLowerCase(Locale.ROOT);
            if (productName.contains("postgresql")) {
                return DatabaseDialect.POSTGRESQL;
            }
            if (productName.contains("mysql") || productName.contains("mariadb")) {
                return DatabaseDialect.MYSQL;
            }
            if (productName.contains("h2")) {
                return DatabaseDialect.H2;
            }
            throw new IllegalStateException("Unsupported database: " + productName);
        } catch (SQLException exception) {
            throw new IllegalStateException("Unable to detect the configured database dialect", exception);
        }
    }
}
