package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.AdminAuthSession;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AdminAuthSessionRepository;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AuthenticationTokenServiceTest {

    @Mock private JwtService jwtService;
    @Mock private AdminAuthSessionRepository sessionRepository;

    private AuthenticationTokenService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticationTokenService(jwtService, sessionRepository);
        ReflectionTestUtils.setField(service, "adminExpiration", 86_400_000L);
    }

    @Test
    void adminLoginCreatesUniqueTwentyFourHourSession() {
        User admin = user(Role.ADMIN);
        Instant expiresAt = Instant.parse("2026-08-09T10:00:00Z");
        when(jwtService.generateToken(anyMap(), eq("admin"), eq(86_400_000L)))
                .thenReturn("admin-token-1", "admin-token-2");
        when(jwtService.extractExpiration("admin-token-1")).thenReturn(Date.from(expiresAt));
        when(jwtService.extractExpiration("admin-token-2")).thenReturn(Date.from(expiresAt.plusSeconds(600)));

        AuthenticationTokenService.IssuedToken first = service.issue(admin);
        AuthenticationTokenService.IssuedToken second = service.issue(admin);

        ArgumentCaptor<AdminAuthSession> sessions = ArgumentCaptor.forClass(AdminAuthSession.class);
        verify(sessionRepository, org.mockito.Mockito.times(2)).save(sessions.capture());
        assertThat(sessions.getAllValues()).extracting(AdminAuthSession::getSessionId).doesNotHaveDuplicates();
        assertThat(first.expiresAt()).isEqualTo(expiresAt);
        assertThat(second.expiresAt()).isEqualTo(expiresAt.plusSeconds(600));
    }

    @Test
    void explicitLogoutRevokesOnlyTheMatchingAdminSession() {
        UUID sessionId = UUID.randomUUID();
        User admin = user(Role.ADMIN);
        AdminAuthSession session = new AdminAuthSession();
        session.setSessionId(sessionId);
        session.setUser(admin);
        session.setExpiresAt(Instant.now().plusSeconds(3600));
        when(jwtService.extractRole("admin-token")).thenReturn("ADMIN");
        when(jwtService.extractTokenId("admin-token")).thenReturn(sessionId.toString());
        when(sessionRepository.findBySessionIdAndUserUsername(sessionId, "admin"))
                .thenReturn(Optional.of(session));

        service.revoke("admin-token", "admin");

        assertThat(session.getRevokedAt()).isNotNull();
        verify(sessionRepository).save(session);
    }

    @Test
    void userAndModeratorKeepTheExistingStatelessTokenPolicy() {
        when(jwtService.generateToken(anyMap(), eq("user"))).thenReturn("user-token");
        when(jwtService.extractExpiration("user-token"))
                .thenReturn(Date.from(Instant.parse("2026-08-09T10:00:00Z")));

        service.issue(user(Role.USER));

        verify(jwtService).generateToken(anyMap(), eq("user"));
        verify(jwtService, never()).generateToken(anyMap(), eq("user"), anyLong());
        verify(sessionRepository, never()).save(any());
        assertThat(service.isSessionActive("moderator-token", user(Role.MODERATOR))).isTrue();
    }

    @Test
    void revokedAndExpiredAdminSessionsAreRejected() {
        UUID sessionId = UUID.randomUUID();
        User admin = user(Role.ADMIN);
        AdminAuthSession revoked = new AdminAuthSession();
        revoked.setSessionId(sessionId);
        revoked.setUser(admin);
        revoked.setExpiresAt(Instant.now().plusSeconds(3600));
        revoked.setRevokedAt(Instant.now());
        when(jwtService.extractTokenId("admin-token")).thenReturn(sessionId.toString());
        when(sessionRepository.findBySessionIdAndUserUsername(sessionId, "admin"))
                .thenReturn(Optional.of(revoked));

        assertThat(service.isSessionActive("admin-token", admin)).isFalse();
    }

    private User user(Role role) {
        User user = new User();
        user.setId(7L);
        user.setUsername(role == Role.ADMIN ? "admin" : role.name().toLowerCase());
        user.setRole(role);
        return user;
    }
}
