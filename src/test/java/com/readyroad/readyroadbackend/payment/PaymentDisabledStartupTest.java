package com.readyroad.readyroadbackend.payment;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class PaymentDisabledStartupTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withUserConfiguration(PaymentConfiguration.class)
                    .withPropertyValues("rijvia.payments.enabled=false");

    @Test
    void startsWithoutStripeConfigurationWhenPaymentsAreDisabled() {
        contextRunner.run(context -> {
            assertThat(context).hasNotFailed();
            assertThat(
                    context.getBeansOfType(
                            PaymentConfiguration.PaymentSettings.class))
                    .isEmpty();
        });
    }
}
