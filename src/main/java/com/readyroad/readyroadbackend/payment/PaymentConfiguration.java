package com.readyroad.readyroadbackend.payment;

import com.stripe.StripeClient;
import java.net.URI;
import java.time.Clock;
import java.util.EnumMap;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@ConditionalOnProperty(name = "rijvia.payments.enabled", havingValue = "true")
@Configuration(proxyBeanMethods = false)
public class PaymentConfiguration {
    @Bean
    public PaymentSettings paymentSettings(Environment env) {
        String key = required(env, "STRIPE_SECRET_KEY");
        String webhook = required(env, "STRIPE_WEBHOOK_SECRET");
        Map<PaymentPlan, String> prices = new EnumMap<>(PaymentPlan.class);
        prices.put(PaymentPlan.RIJVIA_3_DAYS, required(env, "STRIPE_PRICE_ID_3_DAYS"));
        prices.put(PaymentPlan.RIJVIA_1_WEEK, required(env, "STRIPE_PRICE_ID_1_WEEK"));
        prices.put(PaymentPlan.RIJVIA_4_WEEKS, required(env, "STRIPE_PRICE_ID_4_WEEKS"));
        String baseUrl = required(env, "APP_BASE_URL").replaceAll("/+$", "");
        URI uri;
        try { uri = URI.create(baseUrl); }
        catch (IllegalArgumentException ex) { throw new IllegalStateException("APP_BASE_URL must be an absolute http(s) origin"); }
        if (!("https".equals(uri.getScheme()) || "http".equals(uri.getScheme()))
                || uri.getHost() == null || uri.getUserInfo() != null || uri.getQuery() != null
                || uri.getFragment() != null || (uri.getPath() != null && !uri.getPath().isEmpty())) {
            throw new IllegalStateException("APP_BASE_URL must be an absolute http(s) origin");
        }
        return new PaymentSettings(key, webhook, Map.copyOf(prices), baseUrl);
    }

    private static String required(Environment env, String name) {
        String value = env.getProperty(name);
        if (value == null || value.isBlank()) throw new IllegalStateException("Missing required payment environment variable: " + name);
        return value.trim();
    }

    @Bean
    public StripeClient paymentStripeClient(PaymentSettings settings) {
        return new StripeClient(settings.secretKey());
    }

    @Bean("paymentClock")
    @ConditionalOnMissingBean(name = "paymentClock")
    public Clock paymentClock() { return Clock.systemUTC(); }

    // Deliberately no generated toString: configuration must never print credentials.
    public static final class PaymentSettings {
        private final String secretKey;
        private final String webhookSecret;
        private final Map<PaymentPlan, String> prices;
        private final String baseUrl;
        PaymentSettings(String secretKey, String webhookSecret, Map<PaymentPlan, String> prices, String baseUrl) {
            this.secretKey = secretKey; this.webhookSecret = webhookSecret; this.prices = prices; this.baseUrl = baseUrl;
        }
        public String secretKey() { return secretKey; }
        public String webhookSecret() { return webhookSecret; }
        public String priceId(PaymentPlan plan) { return prices.get(plan); }
        public String baseUrl() { return baseUrl; }
    }
}
