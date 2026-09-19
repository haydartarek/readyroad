package com.readyroad.readyroadbackend.payment;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.readyroad.readyroadbackend.config.*;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.service.AdminSystemSettingsService;
import jakarta.servlet.FilterChain;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.mock.web.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = PaymentSecurityTest.Config.class)
@TestPropertySource(properties = "rijvia.payments.enabled=true")
class PaymentSecurityTest {
    @Configuration @EnableWebMvc
    @Import({SecurityConfig.class, PaymentController.class, StripeWebhookController.class})
    static class Config {
        @Bean PaymentConfiguration.PaymentSettings paymentSettings() {
            return new PaymentConfiguration.PaymentSettings("unused", "whsec_offline_fixture", Map.of(), "http://localhost:3000");
        }
    }
    @Autowired WebApplicationContext context;
    @MockitoBean CheckoutService checkout;
    @MockitoBean PurchaseRepository purchases;
    @MockitoBean UserEntitlementRepository entitlements;
    @MockitoBean StripeWebhookService webhooks;
    @MockitoBean JwtAuthenticationFilter jwt;
    @MockitoBean MaintenanceModeFilter maintenance;
    MockMvc mvc;

    @BeforeEach void setup() throws Exception {
        doAnswer(call -> { ((FilterChain) call.getArgument(2)).doFilter(call.getArgument(0), call.getArgument(1)); return null; })
                .when(jwt).doFilter(any(), any(), any());
        doAnswer(call -> { ((FilterChain) call.getArgument(2)).doFilter(call.getArgument(0), call.getArgument(1)); return null; })
                .when(maintenance).doFilter(any(), any(), any());
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }
    @Test void purchaseEndpointsRequireAuthenticationInExistingFilterChain() throws Exception {
        mvc.perform(post("/api/checkout").contentType("application/json")
                .content("{\"plan\":\"RIJVIA_3_DAYS\",\"clientRequestId\":\"req\"}")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/purchases/" + UUID.randomUUID() + "/status")).andExpect(status().isUnauthorized());
        mvc.perform(post("/api/purchases/" + UUID.randomUUID() + "/resume")).andExpect(status().isUnauthorized());
        verifyNoInteractions(checkout, purchases);
    }
    @Test void webhookIsAnonymousButRequiresSignature() throws Exception {
        mvc.perform(post("/api/stripe/webhook").contentType("application/json").content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(webhooks);
    }
    @Test void authenticatedPrincipalOwnsCheckoutRegardlessOfBodyUserId() throws Exception {
        User buyer = new User(); buyer.setId(42L); buyer.setUsername("buyer");
        when(checkout.checkout(42L, PaymentPlan.RIJVIA_3_DAYS, "request-1", "fr"))
                .thenReturn(new CheckoutService.CheckoutResult(UUID.randomUUID(), "https://checkout.stripe.com/c/pay/offline"));
        mvc.perform(post("/api/checkout").with(user(buyer)).header("Accept-Language", "fr").contentType("application/json")
                .content("{\"plan\":\"RIJVIA_3_DAYS\",\"clientRequestId\":\"request-1\",\"userId\":999}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.checkoutUrl").exists());
        verify(checkout).checkout(42L, PaymentPlan.RIJVIA_3_DAYS, "request-1", "fr");
    }
    @Test void authenticatedCheckoutPassesAccountEmailEvenWhenEmailVerificationIsFalse() throws Exception {
        User buyer = new User();
        buyer.setId(42L);
        buyer.setUsername("buyer");
        buyer.setEmail("student@example.com");
        buyer.setEmailVerified(false);

        when(checkout.checkout(
                42L,
                PaymentPlan.RIJVIA_3_DAYS,
                "request-email",
                "en",
                "student@example.com"))
                .thenReturn(new CheckoutService.CheckoutResult(
                        UUID.randomUUID(),
                        "https://checkout.stripe.com/c/pay/offline"));

        mvc.perform(post("/api/checkout")
                        .with(user(buyer))
                        .header("Accept-Language", "en")
                        .contentType("application/json")
                        .content("{\"plan\":\"RIJVIA_3_DAYS\",\"clientRequestId\":\"request-email\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.checkoutUrl").exists());

        verify(checkout).checkout(
                42L,
                PaymentPlan.RIJVIA_3_DAYS,
                "request-email",
                "en",
                "student@example.com");
    }
    @Test void signedWebhookRouteBypassesMaintenanceBlockingOnlyForPost() throws Exception {
        AdminSystemSettingsService settings = mock(AdminSystemSettingsService.class);
        when(settings.isMaintenanceModeEnabled()).thenReturn(true);
        MaintenanceModeFilter filter = new MaintenanceModeFilter(settings);
        FilterChain next = mock(FilterChain.class);
        var request = new MockHttpServletRequest("POST", "/api/stripe/webhook");
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, next);
        verify(next).doFilter(request, response);
        verifyNoInteractions(settings);
    }
}
