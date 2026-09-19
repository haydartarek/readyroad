package com.readyroad.readyroadbackend.payment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.net.Webhook;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/stripe")
public class StripeWebhookController {
    private final StripeWebhookService service;
    private final PaymentConfiguration.PaymentSettings settings;
    public StripeWebhookController(StripeWebhookService service, PaymentConfiguration.PaymentSettings settings) {
        this.service = service; this.settings = settings;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(@RequestBody String body,
            @RequestHeader(value = "Stripe-Signature", defaultValue = "") String signature) {
        try {
            Webhook.Signature.verifyHeader(body, signature, settings.webhookSecret(), 300);
        } catch (SignatureVerificationException | IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Stripe signature");
        }
        StripeWebhookService.CheckoutEvent event;
        try {
            // Parse only after signature verification. These stable Session fields do not depend
            // on the account's API version matching the SDK's complete object model.
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            String id = requiredString(json, "id");
            String type = requiredString(json, "type");
            String sessionId = null, intent = null;
            String purchaseId = null, userId = null, plan = null;
            boolean paid = false;
            if (StripeWebhookService.HANDLED.contains(type)) {
                JsonObject session = json.getAsJsonObject("data").getAsJsonObject("object");
                if (!"checkout.session".equals(requiredString(session, "object"))) throw new IllegalArgumentException();
                sessionId = requiredString(session, "id");
                JsonObject metadata = session.getAsJsonObject("metadata");
                purchaseId = requiredString(metadata, "purchase_id");
                userId = requiredString(metadata, "user_id");
                plan = requiredString(metadata, "plan");
                if (session.has("payment_intent") && !session.get("payment_intent").isJsonNull()) {
                    intent = session.get("payment_intent").isJsonObject()
                            ? requiredString(session.getAsJsonObject("payment_intent"), "id")
                            : session.get("payment_intent").getAsString();
                }
                paid = session.has("payment_status") && "paid".equals(session.get("payment_status").getAsString());
            }
            event = new StripeWebhookService.CheckoutEvent(id, type, sessionId, intent, paid, purchaseId, userId, plan);
        } catch (RuntimeException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Stripe event");
        }
        service.process(event);
        return ResponseEntity.ok().build();
    }

    private static String requiredString(JsonObject object, String name) {
        String value = object.get(name).getAsString();
        if (value.isBlank() || value.length() > 255) throw new IllegalArgumentException();
        return value;
    }
}
