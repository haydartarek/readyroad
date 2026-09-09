package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.Test;

class LearningNotificationTransportTest {
    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(strings = {"ar", "nl", "fr", "en"})
    void emailUsesTheLearnersLocaleAndCanonicalRoute(String locale) throws Exception {
        var jdbc = org.mockito.Mockito.mock(org.springframework.jdbc.core.JdbcTemplate.class);
        var users = org.mockito.Mockito.mock(com.readyroad.readyroadbackend.domain.repository.UserRepository.class);
        var mail = org.mockito.Mockito.mock(org.springframework.mail.javamail.JavaMailSender.class);
        var json = new com.fasterxml.jackson.databind.ObjectMapper();
        var transport = new LearningNotificationTransport(jdbc, users, mail, json,
                org.mockito.Mockito.mock(AdminLearningStore.class));
        org.springframework.test.util.ReflectionTestUtils.setField(transport, "emailEnabled", true);
        org.springframework.test.util.ReflectionTestUtils.setField(transport, "from", "sender@example.test");
        org.springframework.test.util.ReflectionTestUtils.setField(transport, "siteUrl", "https://rijvia.be");
        var user = new com.readyroad.readyroadbackend.domain.entity.User();
        user.setId(1L); user.setPreferredLanguage(locale); user.setEmail("learner@example.test"); user.setEmailVerified(true);
        org.mockito.Mockito.when(users.findById(1L)).thenReturn(java.util.Optional.of(user));
        org.mockito.Mockito.when(jdbc.queryForObject(org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.eq(Boolean.class), org.mockito.ArgumentMatchers.eq(1L))).thenReturn(true);
        var notification = com.readyroad.readyroadbackend.domain.entity.Notification.builder().userId(1L)
                .type(com.readyroad.readyroadbackend.domain.entity.NotificationType.EXAM_PASSED)
                .title("Result").message("Result").messageKey("notif.msg.exam_passed")
                .messageParams("{\"score\":41,\"total\":50,\"pct\":82}").link("/exam/results/7").build();
        assertThat(transport.send("EMAIL", null, notification)).isTrue();
        var message = org.mockito.ArgumentCaptor.forClass(org.springframework.mail.SimpleMailMessage.class);
        org.mockito.Mockito.verify(mail).send(message.capture());
        assertThat(message.getValue().getText()).contains("41", "50")
                .endsWith("https://rijvia.be" + (locale.equals("en") ? "" : "/" + locale) + "/exam/results/7");
        assertThat(message.getValue().getTo()).containsExactly("learner@example.test");
    }

    @Test
    void emailOptOutPreventsDeliveryEvenIfAJobWasAlreadyQueued() throws Exception {
        var jdbc = org.mockito.Mockito.mock(org.springframework.jdbc.core.JdbcTemplate.class);
        var users = org.mockito.Mockito.mock(com.readyroad.readyroadbackend.domain.repository.UserRepository.class);
        var mail = org.mockito.Mockito.mock(org.springframework.mail.javamail.JavaMailSender.class);
        var transport = new LearningNotificationTransport(jdbc, users, mail, new com.fasterxml.jackson.databind.ObjectMapper(),
                org.mockito.Mockito.mock(AdminLearningStore.class));
        org.springframework.test.util.ReflectionTestUtils.setField(transport, "siteUrl", "https://rijvia.be");
        var user = new com.readyroad.readyroadbackend.domain.entity.User();
        user.setId(1L);
        org.mockito.Mockito.when(users.findById(1L)).thenReturn(java.util.Optional.of(user));
        var notification = com.readyroad.readyroadbackend.domain.entity.Notification.builder().userId(1L)
                .type(com.readyroad.readyroadbackend.domain.entity.NotificationType.EXAM_PASSED).message("Result").build();
        assertThat(transport.send("EMAIL", null, notification)).isFalse();
        org.mockito.Mockito.verifyNoInteractions(mail);
    }
    @Test
    void encryptsPayloadAndSignsVapidWithoutMakingANetworkRequest() throws Exception {
        java.security.Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        var generator = java.security.KeyPairGenerator.getInstance("ECDH", "BC");
        generator.initialize(org.bouncycastle.jce.ECNamedCurveTable.getParameterSpec("prime256v1"));
        var server = generator.generateKeyPair();
        var browser = generator.generateKeyPair();
        var encoder = java.util.Base64.getUrlEncoder().withoutPadding();
        String publicKey = encoder.encodeToString(nl.martijndwars.webpush.Utils.encode(
                (org.bouncycastle.jce.interfaces.ECPublicKey) server.getPublic()));
        String privateKey = encoder.encodeToString(nl.martijndwars.webpush.Utils.encode(
                (org.bouncycastle.jce.interfaces.ECPrivateKey) server.getPrivate()));
        byte[] payload = "Test notification".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        var notification = new nl.martijndwars.webpush.Notification(
                "https://fcm.googleapis.com/fcm/send/test", browser.getPublic(), new byte[16], payload);
        var prepared = new LearningNotificationTransport.PreparedPush(publicKey, privateKey, "mailto:test@example.test")
                .prepare(notification);
        assertThat(prepared.getHeaders().get("Content-Encoding")).isEqualTo("aes128gcm");
        assertThat(prepared.getHeaders().get("Authorization")).startsWith("vapid ");
        assertThat(prepared.getBody()).isNotEqualTo(payload);
        assertThatCode(() -> LearningNotificationTransport.validateSubscriptionPublicKey(publicKey))
                .doesNotThrowAnyException();
    }
    @Test
    void acceptsOnlyTrustedHttpsPushProvidersWithoutCredentialsOrRedirectTargets() {
        for (String url : new String[] {"https://fcm.googleapis.com/fcm/send/test",
                "https://updates.push.services.mozilla.com/wpush/v2/test", "https://web.push.apple.com/test"}) {
            assertThatCode(() -> LearningNotificationTransport.validateEndpoint(url)).doesNotThrowAnyException();
        }
        for (String url : new String[] {"http://fcm.googleapis.com/test", "https://127.0.0.1/",
                "https://fcm.googleapis.com.attacker.test/", "https://user@fcm.googleapis.com/",
                "https://fcm.googleapis.com:8080/", "https://fcm.googleapis.com/#secret"}) {
            assertThatThrownBy(() -> LearningNotificationTransport.validateEndpoint(url)).isInstanceOf(IllegalArgumentException.class);
        }
    }
    @Test
    void retryDelayIsExponentialAndBounded() {
        assertThat(LearningNotificationOutbox.retrySeconds(1)).isEqualTo(30);
        assertThat(LearningNotificationOutbox.retrySeconds(2)).isEqualTo(60);
        assertThat(LearningNotificationOutbox.retrySeconds(20)).isEqualTo(3600);
    }
    @Test
    void respectsBoundedProviderRetryAfterWithoutAcceptingNegativeValues() {
        assertThat(LearningNotificationTransport.retryAfterSeconds("120")).isEqualTo(120);
        assertThat(LearningNotificationTransport.retryAfterSeconds("-1")).isZero();
        assertThat(LearningNotificationTransport.retryAfterSeconds("invalid")).isZero();
        assertThat(LearningNotificationTransport.retryAfterSeconds("999999")).isEqualTo(86400);
    }
}
