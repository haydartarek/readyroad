package com.readyroad.readyroadbackend.payment;

import com.stripe.StripeClient;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Component;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(name = "rijvia.payments.enabled", havingValue = "true")
@Component
public class StripeCheckoutGateway {
    private final StripeClient client;
    private final PaymentConfiguration.PaymentSettings settings;

    public StripeCheckoutGateway(
            StripeClient client,
            PaymentConfiguration.PaymentSettings settings) {
        this.client = client;
        this.settings = settings;
    }

    public Session create(Purchase purchase) throws StripeException {
        return create(purchase, null);
    }

    public Session create(Purchase purchase, String customerEmail) throws StripeException {
        String normalizedEmail =
                customerEmail == null || customerEmail.isBlank()
                        ? null
                        : customerEmail.trim();

        String localePrefix =
                "en".equals(purchase.getCheckoutLocale())
                        ? ""
                        : "/" + purchase.getCheckoutLocale();

        String returnPath =
                settings.baseUrl() + localePrefix + "/checkout/";

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)

                .setManagedPayments(
                        SessionCreateParams.ManagedPayments.builder()
                                .setEnabled(false)
                                .build())

                .setCustomerEmail(normalizedEmail)

                .setBrandingSettings(
                        SessionCreateParams.BrandingSettings.builder()
                                .setDisplayName("Rijvia")
                                .setBackgroundColor("#FFFFFF")
                                .setButtonColor("#BC3D1A")
                                .setBorderStyle(
                                        SessionCreateParams.BrandingSettings.BorderStyle.ROUNDED)
                                .setFontFamily(
                                        SessionCreateParams.BrandingSettings.FontFamily.DEFAULT)
                                .build())

                .setLocale(checkoutLocale(purchase.getCheckoutLocale()))

                .setCustomText(
                        SessionCreateParams.CustomText.builder()
                                .setSubmit(
                                        SessionCreateParams.CustomText.Submit.builder()
                                                .setMessage(
                                                        checkoutMessage(
                                                                purchase.getCheckoutLocale()))
                                                .build())
                                .build())

                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(settings.priceId(purchase.getPlan()))
                                .setQuantity(1L)
                                .build())

                .setClientReferenceId(purchase.getId().toString())

                .setSuccessUrl(
                        returnPath
                                + "success?purchaseId="
                                + purchase.getId()
                                + "&session_id={CHECKOUT_SESSION_ID}")

                .setCancelUrl(
                        returnPath
                                + "cancel?purchaseId="
                                + purchase.getId())

                .putMetadata(
                        "purchase_id",
                        purchase.getId().toString())

                .putMetadata(
                        "user_id",
                        purchase.getUserId().toString())

                .putMetadata(
                        "plan",
                        purchase.getPlan().name())

                .setIntegrationIdentifier(
                        "rijvia_paywall_v1_qnvcptaz")

                .build();

        return client.v1()
                .checkout()
                .sessions()
                .create(
                        params,
                        RequestOptions.builder()
                                .setIdempotencyKey(
                                        purchase.getClientRequestId())
                                .setConnectTimeout(5000)
                                .setReadTimeout(15000)
                                .build());
    }

    private static SessionCreateParams.Locale checkoutLocale(String locale) {
        return switch (locale) {
            case "nl" -> SessionCreateParams.Locale.NL;
            case "fr" -> SessionCreateParams.Locale.FR;
            case "en" -> SessionCreateParams.Locale.EN;
            default -> SessionCreateParams.Locale.AUTO;
        };
    }

    private static String checkoutMessage(String locale) {
        return switch (locale) {
            case "ar" ->
                    "دفعة واحدة فقط، بدون اشتراك أو تجديد تلقائي. يبدأ وصولك مباشرة بعد تأكيد الدفع.";
            case "nl" ->
                    "Eenmalige betaling  geen abonnement of automatische verlenging. Je toegang start na bevestiging van de betaling.";
            case "fr" ->
                    "Paiement unique  sans abonnement ni renouvellement automatique. Votre accès commence après confirmation du paiement.";
            default ->
                    "One-time payment  no subscription or automatic renewal. Your access starts after payment is confirmed.";
        };
    }
}