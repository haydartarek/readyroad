package com.readyroad.readyroadbackend.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.service.JwtService;
import com.readyroad.readyroadbackend.service.AuthenticationTokenService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

class JwtAuthenticationFilterTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void missingUserInJwtContinuesWithoutAuthentication() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        AuthenticationTokenService authenticationTokenService = mock(AuthenticationTokenService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, userDetailsService, authenticationTokenService);
        FilterChain filterChain = mock(FilterChain.class);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/auth/me");
        request.addHeader("Authorization", "Bearer stale-token");
        MockHttpServletResponse response = new MockHttpServletResponse();

        when(jwtService.extractUsername("stale-token")).thenReturn("haydar");
        when(userDetailsService.loadUserByUsername("haydar"))
                .thenThrow(new UsernameNotFoundException("User not found: haydar"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).validateToken(anyString(), any());
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    void revokedAdminSessionDoesNotAuthenticate() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsService userDetailsService = mock(UserDetailsService.class);
        AuthenticationTokenService authenticationTokenService = mock(AuthenticationTokenService.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
                jwtService, userDetailsService, authenticationTokenService);
        FilterChain filterChain = mock(FilterChain.class);
        com.readyroad.readyroadbackend.domain.entity.User admin = new com.readyroad.readyroadbackend.domain.entity.User();
        admin.setUsername("admin");
        admin.setRole(com.readyroad.readyroadbackend.domain.enums.Role.ADMIN);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/admin/quiz/questions");
        request.addHeader("Authorization", "Bearer revoked-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(jwtService.extractUsername("revoked-token")).thenReturn("admin");
        when(userDetailsService.loadUserByUsername("admin")).thenReturn(admin);
        when(jwtService.validateToken("revoked-token", admin)).thenReturn(true);
        when(authenticationTokenService.isSessionActive("revoked-token", admin)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }
}
