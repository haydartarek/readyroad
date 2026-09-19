package com.readyroad.readyroadbackend.payment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.google.gson.JsonObject;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.stripe.exception.ApiConnectionException;
import com.stripe.model.checkout.Session;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.test.context.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.testcontainers.junit.jupiter.*;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(classes = PaywallPostgreSqlIntegrationTest.Config.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("postgresql")
@Testcontainers
class PaywallPostgreSqlIntegrationTest {
    @Container static final PostgreSQLContainer POSTGRES = new PostgreSQLContainer("postgres:17.6-alpine");
    static final Instant NOW = Instant.parse("2026-03-28T12:00:00Z");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", PaywallPostgreSqlIntegrationTest::jdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.flyway.url", PaywallPostgreSqlIntegrationTest::jdbcUrl);
        registry.add("spring.flyway.user", POSTGRES::getUsername);
        registry.add("spring.flyway.password", POSTGRES::getPassword);
        registry.add("spring.flyway.default-schema", () -> "readyroad");
        registry.add("spring.flyway.schemas", () -> "readyroad");
        registry.add("spring.jpa.properties.hibernate.default_schema", () -> "readyroad");
    }

    private static String jdbcUrl() {
        String url = POSTGRES.getJdbcUrl();
        return url + (url.contains("?") ? "&" : "?") + "currentSchema=readyroad";
    }

    @Configuration(proxyBeanMethods = false)
    @EnableAutoConfiguration
    @EnableJpaRepositories(basePackageClasses = { PurchaseRepository.class, UserRepository.class },
            includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                    classes = { PurchaseRepository.class, UserEntitlementRepository.class,
                            StripeWebhookEventRepository.class, UserRepository.class }))
    @Import({PaymentConfiguration.class, CheckoutService.class, StripeWebhookService.class})
    static class Config {
        @Bean static PersistenceManagedTypes paymentManagedTypes() {
            return PersistenceManagedTypes.of(Purchase.class.getName(), UserEntitlement.class.getName(),
                    StripeWebhookEvent.class.getName(), User.class.getName());
        }
    }

    @Autowired CheckoutService checkout;
    @Autowired StripeWebhookService webhooks;
    @Autowired PurchaseRepository purchases;
    @Autowired UserEntitlementRepository entitlements;
    @Autowired StripeWebhookEventRepository events;
    @Autowired UserRepository users;
    @Autowired JdbcTemplate jdbc;
    @MockitoBean StripeCheckoutGateway stripe;
    @MockitoBean(name = "paymentClock") Clock clock;
    User user;

    @BeforeEach void setup() throws Exception {
        events.deleteAll();
        purchases.deleteAll();
        entitlements.deleteAll();
        when(clock.instant()).thenReturn(NOW);
        user = newUser();
        when(stripe.create(any())).thenAnswer(call -> session(call.getArgument(0)));
    }

    private User newUser() {
        User created = new User();
        created.setUsername("payment-" + UUID.randomUUID());
        created.setEmail(created.getUsername() + "@example.invalid");
        created.setFullName("Payment integration fixture");
        created.setPasswordHash("offline-fixture");
        return users.saveAndFlush(created);
    }

    private Session session(Purchase purchase) {
        Session session = new Session();
        session.setId("cs_test_" + purchase.getId());
        session.setUrl("https://checkout.stripe.com/c/pay/" + session.getId());
        session.setExpiresAt(NOW.plus(1, ChronoUnit.DAYS).getEpochSecond());
        return session;
    }

    private Purchase purchase(PaymentPlan plan) {
        var result = checkout.checkout(user.getId(), plan, UUID.randomUUID().toString(), "ar");
        return purchases.findById(result.purchaseId()).orElseThrow();
    }

    private StripeWebhookService.CheckoutEvent event(Purchase purchase, String type, boolean paid) {
        return new StripeWebhookService.CheckoutEvent("evt_" + UUID.randomUUID(), "checkout.session." + type,
                purchase.getCheckoutSessionId(), "pi_" + purchase.getId(), paid,
                purchase.getId().toString(), purchase.getUserId().toString(), purchase.getPlan().name());
    }

    private PurchaseStatus status(Purchase purchase) { return purchases.findById(purchase.getId()).orElseThrow().getStatus(); }
    private Instant expiry() { return entitlements.findById(user.getId()).orElseThrow().getExpiresAt(); }

    @Test void migratesIntoReadyroadWithUtcInstants() {
        assertThat(jdbc.queryForObject("select current_schema()", String.class)).isEqualTo("readyroad");
        assertThat(jdbc.queryForObject("select count(*) from information_schema.tables where table_schema='readyroad' "
                + "and table_name in ('purchases','stripe_webhook_events','user_entitlement')", Integer.class)).isEqualTo(3);
        assertThat(jdbc.queryForObject("select count(*) from readyroad.flyway_schema_history where version='66' and success", Integer.class)).isOne();
    }

    @Test void completedUnpaidRemainsPendingWithoutEntitlement() {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        webhooks.process(event(purchase, "completed", false));
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.PENDING);
        assertThat(entitlements.existsById(user.getId())).isFalse();
        assertThat(purchases.findById(purchase.getId()).orElseThrow().getPaymentIntentId()).isEqualTo("pi_" + purchase.getId());
    }

    @Test void delayedSuccessAndDifferentSuccessEventsActivateOnlyOnce() {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        webhooks.process(event(purchase, "completed", false));
        webhooks.process(event(purchase, "async_payment_succeeded", true));
        webhooks.process(event(purchase, "completed", true));
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.PAID);
        assertThat(expiry()).isEqualTo(NOW.plus(3, ChronoUnit.DAYS));
    }

    @ParameterizedTest @ValueSource(strings = {"async_payment_failed", "expired"})
    void pendingFailureNeverActivates(String type) {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_1_WEEK);
        webhooks.process(event(purchase, type, false));
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.FAILED);
        assertThat(entitlements.existsById(user.getId())).isFalse();
    }

    @ParameterizedTest @ValueSource(strings = {"async_payment_failed", "expired"})
    void lateFailureCannotDowngradePaid(String type) {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_1_WEEK);
        webhooks.process(event(purchase, "completed", true));
        webhooks.process(event(purchase, type, false));
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.PAID);
        assertThat(expiry()).isEqualTo(NOW.plus(7, ChronoUnit.DAYS));
    }

    @Test void duplicateEventIsAtomicNoOp() {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_4_WEEKS);
        var event = event(purchase, "completed", true);
        webhooks.process(event);
        webhooks.process(event);
        assertThat(events.count()).isOne();
        assertThat(expiry()).isEqualTo(NOW.plus(28, ChronoUnit.DAYS));
    }

    @Test void ignoresPaymentIntentFailureRatherThanTerminatingCheckout() {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        webhooks.process(new StripeWebhookService.CheckoutEvent("evt_ignored", "payment_intent.payment_failed", null, null, false,
                null, null, null));
        assertThat(events.findById("evt_ignored").orElseThrow().getStatus()).isEqualTo(StripeWebhookEvent.Status.IGNORED);
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.PENDING);
    }

    @Test void enforcesOwnershipPlanAndRetrySessionReuse() throws Exception {
        String requestId = UUID.randomUUID().toString();
        var first = checkout.checkout(user.getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "ar");
        assertThat(checkout.checkout(user.getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "fr")).isEqualTo(first);
        assertThatThrownBy(() -> checkout.checkout(newUser().getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "ar"))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> assertThat(ex.getStatusCode().value()).isEqualTo(404));
        assertThatThrownBy(() -> checkout.checkout(user.getId(), PaymentPlan.RIJVIA_1_WEEK, requestId, "ar"))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> assertThat(ex.getStatusCode().value()).isEqualTo(409));
        verify(stripe, times(1)).create(any());
    }

    @Test void networkFailureRetainsPurchaseAndOriginalLocaleForRetry() throws Exception {
        doThrow(new ApiConnectionException("Offline fixture"))
                .doAnswer(call -> session(call.getArgument(0))).when(stripe).create(any());
        String requestId = UUID.randomUUID().toString();
        assertThatThrownBy(() -> checkout.checkout(user.getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "nl"))
                .isInstanceOf(ResponseStatusException.class);
        Purchase durable = purchases.findByClientRequestId(requestId).orElseThrow();
        assertThat(durable.getCheckoutSessionId()).isNull();
        var retry = checkout.checkout(user.getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "fr");
        assertThat(retry.purchaseId()).isEqualTo(durable.getId());
        assertThat(purchases.findById(durable.getId()).orElseThrow().getCheckoutLocale()).isEqualTo("nl");
    }

    @Test void expiredLinkRequiresNewRequestWithoutAnotherStripeSession() throws Exception {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        when(clock.instant()).thenReturn(NOW.plus(2, ChronoUnit.DAYS));
        assertThatThrownBy(() -> checkout.checkout(user.getId(), purchase.getPlan(), purchase.getClientRequestId(), "ar"))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> assertThat(ex.getStatusCode().value()).isEqualTo(410));
        verify(stripe, times(1)).create(any());
    }

    @Test void concurrentCheckoutRetriesCreateOnlyOneLogicalSession() throws Exception {
        String requestId = UUID.randomUUID().toString();
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var tasks = List.of(1, 2).stream().map(i -> executor.submit(() -> {
                start.await();
                return checkout.checkout(user.getId(), PaymentPlan.RIJVIA_3_DAYS, requestId, "en");
            })).toList();
            start.countDown();
            assertThat(tasks.get(0).get(15, TimeUnit.SECONDS)).isEqualTo(tasks.get(1).get(15, TimeUnit.SECONDS));
        }
        verify(stripe, times(1)).create(any());
        assertThat(purchases.count()).isOne();
    }

    @Test void concurrentFirstEntitlementsSerializeOnUserAndApplyBothExtensions() throws Exception {
        Purchase first = purchase(PaymentPlan.RIJVIA_3_DAYS);
        Purchase second = purchase(PaymentPlan.RIJVIA_1_WEEK);
        CountDownLatch start = new CountDownLatch(1);
        try (var executor = Executors.newFixedThreadPool(2)) {
            var tasks = List.of(first, second).stream().map(p -> executor.submit(() -> {
                start.await(); webhooks.process(event(p, "async_payment_succeeded", true)); return true;
            })).toList();
            start.countDown();
            for (var task : tasks) assertThat(task.get(15, TimeUnit.SECONDS)).isTrue();
        }
        assertThat(entitlements.count()).isOne();
        assertThat(expiry()).isEqualTo(NOW.plus(10, ChronoUnit.DAYS));
        assertThat(status(first)).isEqualTo(PurchaseStatus.PAID);
        assertThat(status(second)).isEqualTo(PurchaseStatus.PAID);
    }

    @Test void renewalUsesFutureExpiryButRestartsExpiredAccessAtNow() {
        UserEntitlement existing = new UserEntitlement();
        existing.setUserId(user.getId());
        existing.setExpiresAt(NOW.plus(2, ChronoUnit.DAYS));
        existing.setStatus(EntitlementStatus.ACTIVE);
        entitlements.saveAndFlush(existing);
        webhooks.process(event(purchase(PaymentPlan.RIJVIA_3_DAYS), "completed", true));
        assertThat(expiry()).isEqualTo(NOW.plus(5, ChronoUnit.DAYS));
        existing = entitlements.findById(user.getId()).orElseThrow();
        existing.setExpiresAt(NOW.minus(1, ChronoUnit.DAYS));
        existing.setStatus(EntitlementStatus.EXPIRED);
        entitlements.saveAndFlush(existing);
        webhooks.process(event(purchase(PaymentPlan.RIJVIA_1_WEEK), "completed", true));
        assertThat(expiry()).isEqualTo(NOW.plus(7, ChronoUnit.DAYS));
    }

    @Test void failedBusinessMutationRollsBackEventAndEntitlementSoStripeCanRetry() {
        Purchase first = purchase(PaymentPlan.RIJVIA_3_DAYS);
        webhooks.process(event(first, "completed", true));
        Purchase second = purchase(PaymentPlan.RIJVIA_1_WEEK);
        var bad = new StripeWebhookService.CheckoutEvent("evt_conflict", "checkout.session.completed",
                second.getCheckoutSessionId(), "pi_" + first.getId(), true,
                second.getId().toString(), second.getUserId().toString(), second.getPlan().name());
        assertThatThrownBy(() -> webhooks.process(bad)).isInstanceOf(RuntimeException.class);
        assertThat(events.existsById(bad.id())).isFalse();
        assertThat(status(second)).isEqualTo(PurchaseStatus.PENDING);
        assertThat(expiry()).isEqualTo(NOW.plus(3, ChronoUnit.DAYS));
        webhooks.process(new StripeWebhookService.CheckoutEvent(bad.id(), bad.type(), bad.sessionId(), "pi_fixed", true,
                bad.purchaseId(), bad.userId(), bad.plan()));
        assertThat(expiry()).isEqualTo(NOW.plus(10, ChronoUnit.DAYS));
    }

    @Test void missingSessionRollsBackEventForLaterRetry() {
        var event = new StripeWebhookService.CheckoutEvent("evt_early", "checkout.session.completed", "cs_not_committed", null, true,
                UUID.randomUUID().toString(), user.getId().toString(), PaymentPlan.RIJVIA_3_DAYS.name());
        assertThatThrownBy(() -> webhooks.process(event)).isInstanceOf(ResponseStatusException.class);
        assertThat(events.existsById(event.id())).isFalse();
    }

    @ParameterizedTest
    @CsvSource({
            "completed, purchase_id", "completed, user_id", "completed, plan",
            "async_payment_succeeded, purchase_id", "async_payment_succeeded, user_id", "async_payment_succeeded, plan",
            "async_payment_failed, purchase_id", "async_payment_failed, user_id", "async_payment_failed, plan",
            "expired, purchase_id", "expired, user_id", "expired, plan"
    })
    void ownershipMismatchReturnsConflictAndRollsBackClaimBeforeAnyTransition(String type, String field) throws Exception {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_1_WEEK);
        var event = event(purchase, type, true);
        JsonObject metadata = new JsonObject();
        metadata.addProperty("purchase_id", event.purchaseId());
        metadata.addProperty("user_id", event.userId());
        metadata.addProperty("plan", event.plan());
        String expected = metadata.get(field).getAsString();
        metadata.addProperty(field, switch (field) {
            case "purchase_id" -> UUID.randomUUID().toString();
            case "user_id" -> newUser().getId().toString();
            case "plan" -> PaymentPlan.RIJVIA_3_DAYS.name();
            default -> throw new IllegalArgumentException(field);
        });
        JsonObject session = new JsonObject();
        session.addProperty("object", "checkout.session");
        session.addProperty("id", event.sessionId());
        session.addProperty("payment_status", "paid");
        session.addProperty("payment_intent", event.paymentIntentId());
        session.add("metadata", metadata);
        JsonObject data = new JsonObject();
        data.add("object", session);
        JsonObject payload = new JsonObject();
        payload.addProperty("id", event.id());
        payload.addProperty("type", event.type());
        payload.add("data", data);
        var mvc = MockMvcBuilders.standaloneSetup(new StripeWebhookController(webhooks,
                new PaymentConfiguration.PaymentSettings("unused", StripeWebhookControllerTest.SECRET, Map.of(), "http://localhost:3000"))).build();
        String body = payload.toString();
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(body)
                .header("Stripe-Signature", StripeWebhookControllerTest.sign(body, Instant.now().getEpochSecond())))
                .andExpect(MockMvcResultMatchers.status().isConflict());
        assertThat(events.existsById(event.id())).isFalse();
        Purchase unchanged = purchases.findById(purchase.getId()).orElseThrow();
        assertThat(unchanged.getStatus()).isEqualTo(PurchaseStatus.PENDING);
        assertThat(unchanged.getPaymentIntentId()).isNull();
        assertThat(unchanged.getUpdatedAt()).isEqualTo(purchase.getUpdatedAt());
        assertThat(entitlements.existsById(user.getId())).isFalse();

        // The same event id must remain retryable once ownership metadata matches.
        metadata.addProperty(field, expected);
        body = payload.toString();
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content(body)
                .header("Stripe-Signature", StripeWebhookControllerTest.sign(body, Instant.now().getEpochSecond())))
                .andExpect(MockMvcResultMatchers.status().isOk());
        assertThat(events.findById(event.id()).orElseThrow().getStatus()).isEqualTo(StripeWebhookEvent.Status.PROCESSED);
        if (type.equals("completed") || type.equals("async_payment_succeeded")) {
            assertThat(status(purchase)).isEqualTo(PurchaseStatus.PAID);
            assertThat(expiry()).isEqualTo(NOW.plus(7, ChronoUnit.DAYS));
        } else {
            assertThat(status(purchase)).isEqualTo(PurchaseStatus.FAILED);
            assertThat(entitlements.existsById(user.getId())).isFalse();
        }
    }

    @ParameterizedTest @ValueSource(strings = {"PAID", "REFUNDED"})
    void terminalPurchaseStillRejectsMismatchedOwnershipWithoutClaimingEvent(String status) {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        purchase.setStatus(PurchaseStatus.valueOf(status));
        purchases.saveAndFlush(purchase);
        var valid = event(purchase, "completed", true);
        var mismatch = new StripeWebhookService.CheckoutEvent(valid.id(), valid.type(), valid.sessionId(), valid.paymentIntentId(), true,
                valid.purchaseId(), newUser().getId().toString(), valid.plan());
        assertThatThrownBy(() -> webhooks.process(mismatch))
                .isInstanceOfSatisfying(ResponseStatusException.class, ex -> assertThat(ex.getStatusCode().value()).isEqualTo(409));
        assertThat(events.existsById(mismatch.id())).isFalse();
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.valueOf(status));
        assertThat(entitlements.existsById(user.getId())).isFalse();
    }

    @Test void refundedRemainsReservedWithoutAnyTransitions() {
        Purchase purchase = purchase(PaymentPlan.RIJVIA_3_DAYS);
        purchase.setStatus(PurchaseStatus.REFUNDED);
        purchases.saveAndFlush(purchase);
        webhooks.process(event(purchase, "completed", true));
        assertThat(status(purchase)).isEqualTo(PurchaseStatus.REFUNDED);
        assertThat(entitlements.existsById(user.getId())).isFalse();
    }
}
