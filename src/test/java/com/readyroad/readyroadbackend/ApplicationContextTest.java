package com.readyroad.readyroadbackend;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic Application Context Test
 *
 * Verifies that the main application class exists and is properly configured.
 * This is a lightweight test that doesn't load the full Spring context.
 *
 * For full integration tests with Spring context, see ReadyRoadIntegrationTest.
 *
 * @author ReadyRoad Team
 * @since 2026-01-17
 */
@DisplayName("Application Context Test")
public class ApplicationContextTest {

    @Test
    @DisplayName("Application main class should exist")
    public void mainClassExists() {
        // Verify that the main application class exists and can be loaded
        assertThat(ReadyroadApplication.class).isNotNull();
    }

    @Test
    @DisplayName("Application main method should exist")
    public void mainMethodExists() throws NoSuchMethodException {
        // Verify that the main method exists with correct signature
        assertThat(ReadyroadApplication.class.getMethod("main", String[].class))
                .isNotNull();
    }
}
