package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.service.LearningNotificationTransport;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users/me/notifications/channels")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class LearningNotificationChannelController {
    private final JdbcTemplate jdbc;
    private final UserRepository users;
    private final LearningNotificationTransport transport;
    @Value("${readyroad.notifications.outbox-enabled:false}") private boolean enabled;

    @GetMapping
    public Map<String, Object> settings(@AuthenticationPrincipal UserDetails principal) {
        long userId = userId(principal);
        boolean optedIn = enabled && Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT COALESCE((SELECT email_enabled FROM learning_notification_preferences WHERE user_id = ?), TRUE)
                """, Boolean.class, userId));
        var user = users.findById(userId).orElseThrow();
        boolean learner = user.getRole() == com.readyroad.readyroadbackend.domain.enums.Role.USER;
        return Map.of("emailEnabled", enabled && learner && optedIn, "emailAvailable", enabled && learner && transport.emailAvailable(),
                "pushAvailable", enabled && learner && transport.pushAvailable(),
                "publicKey", enabled ? transport.publicKey() : "");
    }

    @PutMapping("/email")
    @Transactional
    public void email(@AuthenticationPrincipal UserDetails principal, @RequestBody EmailPreference request) {
        long userId = userId(principal);
        requireLearner(userId);
        if (!enabled || (request.enabled() && !transport.emailAvailable()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email notifications are not available");
        jdbc.update("""
                INSERT INTO learning_notification_preferences(user_id, email_enabled) VALUES (?, ?)
                ON CONFLICT (user_id) DO UPDATE SET email_enabled = EXCLUDED.email_enabled, updated_at = CURRENT_TIMESTAMP
                """, userId, request.enabled());
    }

    @PostMapping("/push")
    @Transactional
    public Map<String, String> subscribe(@AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody PushSubscription request) {
        long userId = userId(principal);
        requireLearner(userId);
        if (!enabled || !transport.pushAvailable())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Push notifications are not configured");
        LearningNotificationTransport.validateEndpoint(request.endpoint());
        byte[] publicKey = decodeKey(request.p256dh(), 65);
        if (publicKey[0] != 4) throw new IllegalArgumentException("Invalid subscription public key");
        LearningNotificationTransport.validateSubscriptionPublicKey(request.p256dh());
        decodeKey(request.auth(), 16);
        String hash = endpointHash(request.endpoint());
        // Lock the owner row for idempotent subscription updates.
        jdbc.queryForObject("SELECT id FROM users WHERE id = ? FOR UPDATE", Long.class, userId);
        var existing = jdbc.queryForList("SELECT id, user_id FROM learning_push_subscriptions WHERE endpoint_hash = ? FOR UPDATE", hash);
        UUID id = UUID.randomUUID();
        if (!existing.isEmpty()) {
            var row = existing.getFirst();
            if (((Number) row.get("user_id")).longValue() != userId)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This browser is subscribed to another account");
            id = (UUID) row.get("id");
            jdbc.update("UPDATE learning_push_subscriptions SET active = TRUE, p256dh = ?, auth_secret = ? WHERE id = ?",
                    request.p256dh(), request.auth(), id);
        } else {
            jdbc.update("""
                    INSERT INTO learning_push_subscriptions(id, user_id, endpoint, endpoint_hash, p256dh, auth_secret)
                    VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT(endpoint_hash) DO NOTHING
                    """, id, userId, request.endpoint(), hash, request.p256dh(), request.auth());
            var owner = jdbc.queryForMap("SELECT id, user_id FROM learning_push_subscriptions WHERE endpoint_hash = ?", hash);
            if (((Number) owner.get("user_id")).longValue() != userId)
                throw new ResponseStatusException(HttpStatus.CONFLICT, "This browser is subscribed to another account");
            id = (UUID) owner.get("id");
        }
        return Map.of("id", id.toString());
    }

    @DeleteMapping("/push")
    @Transactional
    public void unsubscribe(@AuthenticationPrincipal UserDetails principal, @Valid @RequestBody Endpoint request) {
        if (!enabled) return;
        jdbc.update("UPDATE learning_push_subscriptions SET active = FALSE WHERE user_id = ? AND endpoint_hash = ?",
                userId(principal), endpointHash(request.endpoint()));
    }

    @PostMapping("/push/status")
    public Map<String, Boolean> subscriptionStatus(@AuthenticationPrincipal UserDetails principal,
            @Valid @RequestBody Endpoint request) {
        boolean subscribed = enabled && Boolean.TRUE.equals(jdbc.queryForObject("""
                SELECT EXISTS(SELECT 1 FROM learning_push_subscriptions WHERE user_id = ? AND endpoint_hash = ? AND active)
                """, Boolean.class, userId(principal), endpointHash(request.endpoint())));
        return Map.of("subscribed", subscribed);
    }

    private long userId(UserDetails principal) {
        if (principal == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        return users.findByUsername(principal.getUsername()).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.UNAUTHORIZED)).getId();
    }

    private void requireLearner(long id) {
        if (users.findById(id).orElseThrow().getRole() != com.readyroad.readyroadbackend.domain.enums.Role.USER)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Learning delivery is available to learner accounts");
    }

    static byte[] decodeKey(String value, int length) {
        try {
            byte[] bytes = Base64.getUrlDecoder().decode(value);
            if (bytes.length != length) throw new IllegalArgumentException("Invalid subscription key length");
            return bytes;
        } catch (IllegalArgumentException ex) { throw new IllegalArgumentException("Invalid subscription key"); }
    }

    private static String endpointHash(String endpoint) {
        try { return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(endpoint.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.NoSuchAlgorithmException ex) { throw new IllegalStateException(ex); }
    }

    public record EmailPreference(boolean enabled) {}
    public record Endpoint(@NotBlank @Size(max = 2048) String endpoint) {}
    public record PushSubscription(@NotBlank @Size(max = 2048) String endpoint,
            @NotBlank @Size(max = 100) String p256dh, @NotBlank @Size(max = 32) String auth) {}
}
