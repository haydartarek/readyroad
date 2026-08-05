package com.readyroad.readyroadbackend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.AuthIdentity;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.GoogleAuthExchangeRequest;
import com.readyroad.readyroadbackend.exception.SocialAuthException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class SocialAuthServiceTest {

        private static final GoogleAuthExchangeRequest REQUEST = new GoogleAuthExchangeRequest(
                        "sample-code",
                        "http://localhost:3000/api/auth/google/callback",
                        "sample-verifier");
        private static final GoogleAuthExchangeRequest ARABIC_REQUEST = new GoogleAuthExchangeRequest(
                        "sample-code",
                        "http://localhost:3000/api/auth/google/callback",
                        "sample-verifier",
                        "ar");

        @Mock
        private GoogleOAuthService googleOAuthService;

        @Mock
        private UserRepository userRepository;

        @Mock
        private AuthIdentityRepository authIdentityRepository;

        @Mock
        private PasswordEncoder passwordEncoder;

        @Mock
        private JwtService jwtService;

        @Mock
        private NotificationService notificationService;

        @Mock
        private BackendMessageService messages;

        @InjectMocks
        private SocialAuthService socialAuthService;

        @Test
        @DisplayName("authenticateWithGoogle blocks automatic merge when email already belongs to password account")
        void authenticateWithGoogleBlocksExistingPasswordAccount() {
                User existingUser = new User();
                existingUser.setId(10L);
                existingUser.setUsername("existing_user");
                existingUser.setEmail("existing@readyroad.be");

                when(googleOAuthService.exchangeCodeForUser(REQUEST)).thenReturn(googleUser(
                                "google-user-1",
                                "existing@readyroad.be",
                                true));
                when(authIdentityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "google-user-1"))
                                .thenReturn(Optional.empty());
                when(userRepository.findByEmailIgnoreCase("existing@readyroad.be"))
                                .thenReturn(Optional.of(existingUser));

                assertThatThrownBy(() -> socialAuthService.authenticateWithGoogle(REQUEST))
                                .isInstanceOf(SocialAuthException.class)
                                .satisfies(throwable -> {
                                        SocialAuthException ex = (SocialAuthException) throwable;
                                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                                        assertThat(ex.getCode()).isEqualTo("ACCOUNT_EXISTS_WITH_PASSWORD");
                                });
        }

        @Test
        @DisplayName("authenticateWithGoogle preserves an existing account preference")
        void authenticateWithGooglePreservesExistingPreference() {
                User user = new User();
                user.setId(11L);
                user.setUsername("existing_google");
                user.setEmail("google@readyroad.be");
                user.setFullName("Google User");
                user.setRole(Role.USER);
                user.setPreferredLanguage("nl");

                AuthIdentity identity = new AuthIdentity();
                identity.setUser(user);
                identity.setProvider(AuthProvider.GOOGLE);
                identity.setProviderUserId("google-user-existing");

                when(googleOAuthService.exchangeCodeForUser(ARABIC_REQUEST)).thenReturn(googleUser(
                                "google-user-existing",
                                "google@readyroad.be",
                                true));
                when(authIdentityRepository.findByProviderAndProviderUserId(
                                AuthProvider.GOOGLE,
                                "google-user-existing"))
                                .thenReturn(Optional.of(identity));
                when(authIdentityRepository.save(identity)).thenReturn(identity);
                when(authIdentityRepository.findByUserId(11L)).thenReturn(List.of(identity));
                when(jwtService.generateToken(anyMap(), anyString())).thenReturn("jwt-token");

                AuthResponse response = socialAuthService.authenticateWithGoogle(ARABIC_REQUEST);

                assertThat(response.getPreferredLanguage()).isEqualTo("nl");
                assertThat(user.getPreferredLanguage()).isEqualTo("nl");
                assertThat(user.getEmailVerified()).isTrue();
                verify(userRepository).save(user);
        }

        @Test
        @DisplayName("authenticateWithGoogle creates a new ReadyRoad account when no conflict exists")
        void authenticateWithGoogleCreatesNewUser() {
                when(googleOAuthService.exchangeCodeForUser(ARABIC_REQUEST)).thenReturn(googleUser(
                                "google-user-2",
                                "newuser@readyroad.be",
                                true));
                when(authIdentityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "google-user-2"))
                                .thenReturn(Optional.empty());
                when(userRepository.findByEmailIgnoreCase("newuser@readyroad.be"))
                                .thenReturn(Optional.empty());
                when(userRepository.existsByUsernameIgnoreCase(anyString())).thenReturn(false);
                when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
                when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                        User user = invocation.getArgument(0);
                        user.setId(42L);
                        return user;
                });
                when(authIdentityRepository.save(any(AuthIdentity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));
                when(authIdentityRepository.findByUserId(42L)).thenReturn(List.of(savedIdentity(42L, "google-user-2")));
                when(jwtService.generateToken(anyMap(), anyString())).thenReturn("jwt-token");

                AuthResponse response = socialAuthService.authenticateWithGoogle(ARABIC_REQUEST);

                assertThat(response.getToken()).isEqualTo("jwt-token");
                assertThat(response.getNewUser()).isTrue();
                assertThat(response.getPreferredLanguage()).isEqualTo("ar");
                assertThat(response.getLinkedProviders()).containsExactly("GOOGLE");
                verify(notificationService).notifyAdminsNewUser(anyString(), anyString());
        }

        @Test
        @DisplayName("linkGoogleToCurrentUser rejects linking when the Google email does not match the current account")
        void linkGoogleToCurrentUserRejectsEmailMismatch() {
                User currentUser = new User();
                currentUser.setId(7L);
                currentUser.setUsername("member");
                currentUser.setEmail("member@readyroad.be");

                when(userRepository.findById(7L)).thenReturn(Optional.of(currentUser));
                when(googleOAuthService.exchangeCodeForUser(REQUEST)).thenReturn(googleUser(
                                "google-user-3",
                                "other@readyroad.be",
                                true));
                when(authIdentityRepository.findByUserIdAndProvider(7L, AuthProvider.GOOGLE))
                                .thenReturn(Optional.empty());
                when(authIdentityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "google-user-3"))
                                .thenReturn(Optional.empty());

                assertThatThrownBy(() -> socialAuthService.linkGoogleToCurrentUser(7L, REQUEST))
                                .isInstanceOf(SocialAuthException.class)
                                .satisfies(throwable -> {
                                        SocialAuthException ex = (SocialAuthException) throwable;
                                        assertThat(ex.getStatus()).isEqualTo(HttpStatus.CONFLICT);
                                        assertThat(ex.getCode()).isEqualTo("GOOGLE_EMAIL_MISMATCH");
                                });

                verify(authIdentityRepository, never()).save(any(AuthIdentity.class));
        }

        @Test
        @DisplayName("linkGoogleToCurrentUser stores a Google identity when the verified email matches")
        void linkGoogleToCurrentUserStoresIdentity() {
                User currentUser = new User();
                currentUser.setId(7L);
                currentUser.setUsername("member");
                currentUser.setEmail("member@readyroad.be");

                when(userRepository.findById(7L)).thenReturn(Optional.of(currentUser));
                when(googleOAuthService.exchangeCodeForUser(REQUEST)).thenReturn(googleUser(
                                "google-user-4",
                                "member@readyroad.be",
                                true));
                when(authIdentityRepository.findByUserIdAndProvider(7L, AuthProvider.GOOGLE))
                                .thenReturn(Optional.empty());
                when(authIdentityRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "google-user-4"))
                                .thenReturn(Optional.empty());
                when(authIdentityRepository.save(any(AuthIdentity.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                User result = socialAuthService.linkGoogleToCurrentUser(7L, REQUEST);

                assertThat(result.getId()).isEqualTo(7L);
                verify(authIdentityRepository).save(any(AuthIdentity.class));
        }

        private GoogleOAuthService.GoogleUserInfo googleUser(String providerUserId, String email, boolean verified) {
                return new GoogleOAuthService.GoogleUserInfo(
                                providerUserId,
                                email,
                                verified,
                                "Ready Road",
                                "Ready",
                                "Road",
                                null);
        }

        private AuthIdentity savedIdentity(Long userId, String providerUserId) {
                User user = new User();
                user.setId(userId);
                user.setUsername("newuser");
                user.setEmail("newuser@readyroad.be");
                user.setRole(Role.USER);

                AuthIdentity identity = new AuthIdentity();
                identity.setUser(user);
                identity.setProvider(AuthProvider.GOOGLE);
                identity.setProviderUserId(providerUserId);
                identity.setProviderEmail("newuser@readyroad.be");
                identity.setEmailVerified(true);
                return identity;
        }
}
