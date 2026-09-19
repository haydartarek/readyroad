package com.readyroad.readyroadbackend.payment;

import static org.assertj.core.api.Assertions.*;

import com.stripe.StripeClient;
import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class StripeCheckoutGatewayTest {
    @ParameterizedTest
    @CsvSource({"en, /checkout/", "ar, /ar/checkout/", "nl, /nl/checkout/", "fr, /fr/checkout/"})
    void sendsOneTimePriceValidatedReturnUrlsOwnershipMetadataAndStableStripeIdempotencyKey(String locale, String returnPath) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        AtomicReference<String> requestBody = new AtomicReference<>();
        AtomicReference<String> idempotency = new AtomicReference<>();
        server.createContext("/v1/checkout/sessions", exchange -> {
            requestBody.set(URLDecoder.decode(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8), StandardCharsets.UTF_8));
            idempotency.set(exchange.getRequestHeaders().getFirst("Idempotency-Key"));
            byte[] response = "{\"id\":\"cs_offline_fixture\",\"object\":\"checkout.session\",\"url\":\"https://checkout.stripe.com/c/pay/offline\"}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.length);
            exchange.getResponseBody().write(response);
            exchange.close();
        });
        server.start();
        try {
            StripeClient client = StripeClient.builder().setApiKey("sk_test_offline_fixture_not_a_real_key")
                    .setApiBase("http://127.0.0.1:" + server.getAddress().getPort()).build();
            var settings = new PaymentConfiguration.PaymentSettings("unused", "unused",
                    Map.of(PaymentPlan.RIJVIA_1_WEEK, "price_configured_week"), "https://rijvia.example");
            Purchase purchase = new Purchase();
            purchase.setId(UUID.randomUUID()); purchase.setPlan(PaymentPlan.RIJVIA_1_WEEK);
            purchase.setUserId(42L);
            purchase.setCheckoutLocale(locale); purchase.setClientRequestId("stable-request");
            assertThat(new StripeCheckoutGateway(client, settings).create(purchase, "student@example.com").getId()).isEqualTo("cs_offline_fixture");
            assertThat(idempotency.get()).isEqualTo("stable-request");

            String expectedStripeLocale = switch (locale) {
                case "nl" -> "nl";
                case "fr" -> "fr";
                case "en" -> "en";
                default -> "auto";
            };

            assertThat(requestBody.get()).contains(
                    "mode=payment",
                    "line_items[0][price]=price_configured_week",
                    "line_items[0][quantity]=1",
                    "integration_identifier=rijvia_paywall_v1_qnvcptaz",
                    "managed_payments[enabled]=false",
                    "customer_email=student@example.com",
                    "branding_settings[display_name]=Rijvia",
                    "branding_settings[background_color]=#FFFFFF",
                    "branding_settings[button_color]=#BC3D1A",
                    "branding_settings[border_style]=rounded",
                    "branding_settings[font_family]=default",
                    "custom_text[submit][message]=",
                    "locale=" + expectedStripeLocale,
                    "success_url=https://rijvia.example" + returnPath + "success?purchaseId=" + purchase.getId() + "&session_id={CHECKOUT_SESSION_ID}",
                    "cancel_url=https://rijvia.example" + returnPath + "cancel?purchaseId=" + purchase.getId(),
                    "metadata[purchase_id]=" + purchase.getId(), "metadata[user_id]=42", "metadata[plan]=RIJVIA_1_WEEK")
                    .doesNotContain("payment_method_types", "mode=subscription", "price_data", "/en/checkout/");
        } finally { server.stop(0); }
    }
}
