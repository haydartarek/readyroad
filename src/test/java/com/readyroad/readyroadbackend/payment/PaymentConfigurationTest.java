package com.readyroad.readyroadbackend.payment;

import static org.assertj.core.api.Assertions.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.mock.env.MockEnvironment;

class PaymentConfigurationTest {
    private final PaymentConfiguration config = new PaymentConfiguration();
    private MockEnvironment environment() {
        MockEnvironment env = new MockEnvironment();
        for (String name : List.of("STRIPE_SECRET_KEY", "STRIPE_WEBHOOK_SECRET", "STRIPE_PRICE_ID_3_DAYS",
                "STRIPE_PRICE_ID_1_WEEK", "STRIPE_PRICE_ID_4_WEEKS")) env.setProperty(name, "offline-fixture");
        env.setProperty("APP_BASE_URL", "http://localhost:3000");
        return env;
    }
    @ParameterizedTest
    @ValueSource(strings = {"STRIPE_SECRET_KEY", "STRIPE_WEBHOOK_SECRET", "STRIPE_PRICE_ID_3_DAYS",
            "STRIPE_PRICE_ID_1_WEEK", "STRIPE_PRICE_ID_4_WEEKS", "APP_BASE_URL"})
    void failsClearlyForEachMissingVariable(String name) {
        MockEnvironment env = environment();
        env.setProperty(name, " ");
        assertThatThrownBy(() -> config.paymentSettings(env)).isInstanceOf(IllegalStateException.class).hasMessageContaining(name);
    }
    @ParameterizedTest @ValueSource(strings = {"javascript:alert(1)", "https://user@example.com", "https://example.com/path", "https://example.com?redirect=evil", "//example.com"})
    void rejectsUnsafeReturnOrigins(String value) {
        assertThatThrownBy(() -> config.paymentSettings(environment().withProperty("APP_BASE_URL", value)))
                .isInstanceOf(IllegalStateException.class);
    }
    @Test void configurationDoesNotPrintSecrets() {
        assertThat(config.paymentSettings(environment()).toString()).doesNotContain("offline-fixture");
    }
}
