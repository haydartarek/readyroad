package com.readyroad.readyroadbackend.payment;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Map;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class StripeWebhookControllerTest {
    static final String SECRET = "whsec_offline_fixture_not_a_real_secret";
    final StripeWebhookService service = mock(StripeWebhookService.class);
    final MockMvc mvc = MockMvcBuilders.standaloneSetup(new StripeWebhookController(service,
            new PaymentConfiguration.PaymentSettings("unused", SECRET, Map.of(), "http://localhost:3000"))).build();
    static final String BODY = """
            {"id":"evt_signed","type":"checkout.session.completed","data":{"object":{
            "object":"checkout.session","id":"cs_test_example","payment_status":"paid","payment_intent":"pi_test_example",
            "metadata":{"purchase_id":"6b1451f3-4449-4429-bc1d-d2d4ec10578c","user_id":"42","plan":"RIJVIA_1_WEEK"}}}}
            """;

    static String sign(String body, long time) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return "t=" + time + ",v1=" + HexFormat.of().formatHex(mac.doFinal((time + "." + body).getBytes(StandardCharsets.UTF_8)));
    }

    @Test void acceptsVerifiedRawBody() throws Exception {
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY)
                .header("Stripe-Signature", sign(BODY, Instant.now().getEpochSecond()))).andExpect(status().isOk());
        verify(service).process(new StripeWebhookService.CheckoutEvent("evt_signed", "checkout.session.completed", "cs_test_example", "pi_test_example", true,
                "6b1451f3-4449-4429-bc1d-d2d4ec10578c", "42", "RIJVIA_1_WEEK"));
    }
    @ParameterizedTest @ValueSource(strings = {"purchase_id", "user_id", "plan"})
    void rejectsMissingOrBlankOwnershipMetadata(String field) throws Exception {
        for (boolean missing : new boolean[] {true, false}) {
            JsonObject json = JsonParser.parseString(BODY).getAsJsonObject();
            JsonObject metadata = json.getAsJsonObject("data").getAsJsonObject("object").getAsJsonObject("metadata");
            if (missing) metadata.remove(field);
            else metadata.addProperty(field, " ");
            String body = json.toString();
            mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(body)
                    .header("Stripe-Signature", sign(body, Instant.now().getEpochSecond()))).andExpect(status().isBadRequest());
        }
        verifyNoInteractions(service);
    }
    @Test void rejectsMissingInvalidTamperedAndExpiredSignaturesWithoutProcessing() throws Exception {
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY)).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY).header("Stripe-Signature", "invalid")).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY + " ")
                .header("Stripe-Signature", sign(BODY, Instant.now().getEpochSecond()))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY)
                .header("Stripe-Signature", sign(BODY, Instant.now().minusSeconds(600).getEpochSecond()))).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void validSignatureDoesNotTurnMalformedPayloadIntoSuccess() throws Exception {
        String body = "{broken";
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(body)
                .header("Stripe-Signature", sign(body, Instant.now().getEpochSecond()))).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void businessFailureIsNotAcknowledgedAsSuccess() throws Exception {
        doThrow(new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE))
                .when(service).process(any());
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(BODY)
                .header("Stripe-Signature", sign(BODY, Instant.now().getEpochSecond()))).andExpect(status().isServiceUnavailable());
    }
}
