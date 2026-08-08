package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;

import io.jsonwebtoken.Claims;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class JwtServiceAdminExpirationTest {

    @Test
    void explicitAdminLifetimeIsExactlyTwentyFourHours() {
        JwtService jwtService = new JwtService();
        String secret = Base64.getEncoder().encodeToString(
                "readyroad-test-secret-key-at-least-32-bytes".getBytes(StandardCharsets.UTF_8));
        ReflectionTestUtils.setField(jwtService, "secret", secret);
        ReflectionTestUtils.setField(jwtService, "expiration", 3_600_000L);
        ReflectionTestUtils.setField(jwtService, "issuer", "readyroad-test");
        jwtService.init();

        String token = jwtService.generateToken(
                Map.of("role", "ADMIN", "jti", "f4ac7cd8-b14f-4d66-9dc9-e06cbf7bd403"),
                "admin",
                86_400_000L);

        long lifetime = jwtService.extractExpiration(token).getTime()
                - jwtService.extractClaim(token, Claims::getIssuedAt).getTime();
        assertThat(lifetime).isEqualTo(86_400_000L);
    }
}
