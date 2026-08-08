package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.AdminAuthSession;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AdminAuthSessionRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationTokenService {

    private final JwtService jwtService;
    private final AdminAuthSessionRepository adminAuthSessionRepository;

    @Value("${jwt.admin-expiration:86400000}")
    private long adminExpiration;

    @Transactional
    public IssuedToken issue(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());

        String token;
        if (user.getRole() == Role.ADMIN) {
            UUID sessionId = UUID.randomUUID();
            claims.put("jti", sessionId.toString());
            token = jwtService.generateToken(claims, user.getUsername(), adminExpiration);

            AdminAuthSession session = new AdminAuthSession();
            session.setSessionId(sessionId);
            session.setUser(user);
            session.setExpiresAt(jwtService.extractExpiration(token).toInstant());
            adminAuthSessionRepository.save(session);
        } else {
            token = jwtService.generateToken(claims, user.getUsername());
        }

        return new IssuedToken(token, jwtService.extractExpiration(token).toInstant());
    }

    @Transactional(readOnly = true)
    public boolean isSessionActive(String token, UserDetails userDetails) {
        if (!(userDetails instanceof User user) || user.getRole() != Role.ADMIN) {
            return true;
        }

        Optional<UUID> sessionId = parseSessionId(jwtService.extractTokenId(token));
        if (sessionId.isEmpty()) {
            return false;
        }

        return adminAuthSessionRepository
                .findBySessionIdAndUserUsername(sessionId.get(), user.getUsername())
                .filter(session -> session.getRevokedAt() == null)
                .filter(session -> session.getExpiresAt().isAfter(Instant.now()))
                .isPresent();
    }

    @Transactional
    public void revoke(String token, String username) {
        if (!Role.ADMIN.name().equals(jwtService.extractRole(token))) {
            return;
        }

        parseSessionId(jwtService.extractTokenId(token))
                .flatMap(sessionId -> adminAuthSessionRepository
                        .findBySessionIdAndUserUsername(sessionId, username))
                .filter(session -> session.getRevokedAt() == null)
                .ifPresent(session -> {
                    session.setRevokedAt(Instant.now());
                    adminAuthSessionRepository.save(session);
                });
    }

    private Optional<UUID> parseSessionId(String value) {
        if (value == null || value.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(value));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public record IssuedToken(String value, Instant expiresAt) {
    }
}
