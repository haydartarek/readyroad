package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.AuthIdentity;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.AuthProvider;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.AuthIdentityRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AuthResponse;
import com.readyroad.readyroadbackend.dto.GoogleAuthExchangeRequest;
import com.readyroad.readyroadbackend.exception.SocialAuthException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialAuthService {

    private final GoogleOAuthService googleOAuthService;
    private final UserRepository userRepository;
    private final AuthIdentityRepository authIdentityRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final BackendMessageService messages;

    @Transactional
    public AuthResponse authenticateWithGoogle(GoogleAuthExchangeRequest request) {
        GoogleOAuthService.GoogleUserInfo googleUser = googleOAuthService.exchangeCodeForUser(request);
        validateVerifiedEmail(googleUser);

        String normalizedEmail = normalizeEmail(googleUser.email());
        Optional<AuthIdentity> existingIdentity = authIdentityRepository.findByProviderAndProviderUserId(
                AuthProvider.GOOGLE,
                googleUser.providerUserId());

        if (existingIdentity.isPresent()) {
            AuthIdentity identity = existingIdentity.get();
            identity.setProviderEmail(normalizedEmail);
            identity.setEmailVerified(Boolean.TRUE.equals(googleUser.emailVerified()));
            authIdentityRepository.save(identity);

            return buildAuthResponse(identity.getUser(), false);
        }

        Optional<User> existingUser = userRepository.findByEmailIgnoreCase(normalizedEmail);
        if (existingUser.isPresent()) {
            log.info("Google login blocked because email already belongs to a password account: {}", normalizedEmail);
            throw new SocialAuthException(
                    HttpStatus.CONFLICT,
                    "ACCOUNT_EXISTS_WITH_PASSWORD",
                    messages.get("auth.google.account_exists_with_password"));
        }

        User newUser = createGoogleUser(googleUser, normalizedEmail, request.preferredLanguage());
        createGoogleIdentity(newUser, googleUser, normalizedEmail);

        try {
            notificationService.notifyAdminsNewUser(newUser.getUsername(), newUser.getEmail());
        } catch (Exception ex) {
            log.warn("Admin notification for Google signup failed: {}", ex.getMessage());
        }

        return buildAuthResponse(newUser, true);
    }

    @Transactional
    public User linkGoogleToCurrentUser(Long userId, GoogleAuthExchangeRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new SocialAuthException(
                        HttpStatus.NOT_FOUND,
                        "USER_NOT_FOUND",
                        messages.get("auth.user_not_found")));

        GoogleOAuthService.GoogleUserInfo googleUser = googleOAuthService.exchangeCodeForUser(request);
        validateVerifiedEmail(googleUser);

        String normalizedEmail = normalizeEmail(googleUser.email());
        Optional<AuthIdentity> existingUserGoogle = authIdentityRepository.findByUserIdAndProvider(userId,
                AuthProvider.GOOGLE);
        if (existingUserGoogle.isPresent()) {
            AuthIdentity linkedIdentity = existingUserGoogle.get();
            if (linkedIdentity.getProviderUserId().equals(googleUser.providerUserId())) {
                linkedIdentity.setProviderEmail(normalizedEmail);
                linkedIdentity.setEmailVerified(true);
                authIdentityRepository.save(linkedIdentity);
                return user;
            }

            throw new SocialAuthException(
                    HttpStatus.CONFLICT,
                    "GOOGLE_ALREADY_LINKED",
                    messages.get("auth.google.provider_already_linked"));
        }

        Optional<AuthIdentity> existingIdentity = authIdentityRepository.findByProviderAndProviderUserId(
                AuthProvider.GOOGLE,
                googleUser.providerUserId());

        if (existingIdentity.isPresent() && !existingIdentity.get().getUser().getId().equals(userId)) {
            throw new SocialAuthException(
                    HttpStatus.CONFLICT,
                    "GOOGLE_ACCOUNT_LINKED_TO_ANOTHER_USER",
                    messages.get("auth.google.provider_account_linked_elsewhere"));
        }

        if (!user.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new SocialAuthException(
                    HttpStatus.CONFLICT,
                    "GOOGLE_EMAIL_MISMATCH",
                    messages.get("auth.google.email_mismatch"));
        }

        createGoogleIdentity(user, googleUser, normalizedEmail);
        return user;
    }

    public List<String> getLinkedProviders(Long userId) {
        return authIdentityRepository.findByUserId(userId).stream()
                .map(identity -> identity.getProvider().name())
                .sorted()
                .toList();
    }

    private void validateVerifiedEmail(GoogleOAuthService.GoogleUserInfo googleUser) {
        if (!Boolean.TRUE.equals(googleUser.emailVerified())) {
            throw new SocialAuthException(
                    HttpStatus.UNPROCESSABLE_CONTENT,
                    "GOOGLE_EMAIL_NOT_VERIFIED",
                    messages.get("auth.google.email_not_verified"));
        }
    }

    private User createGoogleUser(
            GoogleOAuthService.GoogleUserInfo googleUser,
            String normalizedEmail,
            String preferredLanguage) {
        User user = new User();
        user.setUsername(generateAvailableUsername(googleUser, normalizedEmail));
        user.setEmail(normalizedEmail);
        user.setFullName(resolveFullName(googleUser, normalizedEmail));
        user.setPasswordHash(passwordEncoder.encode(generateSocialPlaceholderPassword()));
        user.setRole(Role.USER);
        user.setIsActive(true);
        user.setIsLocked(false);
        user.setPreferredLanguage(preferredLanguage);

        return userRepository.save(user);
    }

    private void createGoogleIdentity(User user, GoogleOAuthService.GoogleUserInfo googleUser, String normalizedEmail) {
        AuthIdentity identity = new AuthIdentity();
        identity.setUser(user);
        identity.setProvider(AuthProvider.GOOGLE);
        identity.setProviderUserId(googleUser.providerUserId());
        identity.setProviderEmail(normalizedEmail);
        identity.setEmailVerified(Boolean.TRUE.equals(googleUser.emailVerified()));
        authIdentityRepository.save(identity);
    }

    private AuthResponse buildAuthResponse(User user, boolean newUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        String jwtToken = jwtService.generateToken(claims, user.getUsername());

        return AuthResponse.of(
                jwtToken,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getPreferredLanguage(),
                getLinkedProviders(user.getId()),
                newUser);
    }

    private String resolveFullName(GoogleOAuthService.GoogleUserInfo googleUser, String normalizedEmail) {
        if (StringUtils.hasText(googleUser.name())) {
            return googleUser.name().trim().replaceAll("\\s+", " ");
        }

        String emailLocalPart = normalizedEmail.substring(0, normalizedEmail.indexOf('@'));
        return emailLocalPart.replace('.', ' ').replace('_', ' ');
    }

    private String generateAvailableUsername(
            GoogleOAuthService.GoogleUserInfo googleUser,
            String normalizedEmail) {
        String seed = normalizedEmail.substring(0, normalizedEmail.indexOf('@'));
        if (StringUtils.hasText(googleUser.givenName())) {
            seed = googleUser.givenName();
        } else if (StringUtils.hasText(googleUser.name())) {
            seed = googleUser.name();
        }

        String sanitized = sanitizeUsername(seed);
        for (int attempt = 0; attempt < 200; attempt++) {
            String candidate = attempt == 0 ? sanitized : appendSuffix(sanitized, attempt + 1);
            if (!userRepository.existsByUsernameIgnoreCase(candidate)) {
                return candidate;
            }
        }

        return "readyroad" + System.currentTimeMillis();
    }

    private String sanitizeUsername(String raw) {
        String value = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        value = value.replaceAll("[^a-z0-9_]", "_");
        value = value.replaceAll("_{2,}", "_");
        value = value.replaceAll("^_+", "").replaceAll("_+$", "");

        if (!StringUtils.hasText(value)) {
            value = "readyroad";
        }

        if (Character.isDigit(value.charAt(0))) {
            value = "rr_" + value;
        }

        if (value.length() < 4) {
            value = (value + "_rrr").substring(0, 4);
        }

        if (value.length() > 20) {
            value = value.substring(0, 20).replaceAll("_+$", "");
        }

        return StringUtils.hasText(value) ? value : "readyroad";
    }

    private String appendSuffix(String base, int suffixNumber) {
        String suffix = String.valueOf(suffixNumber);
        int maxBaseLength = Math.max(1, 20 - suffix.length());
        String trimmedBase = base.length() > maxBaseLength ? base.substring(0, maxBaseLength) : base;
        return trimmedBase + suffix;
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String generateSocialPlaceholderPassword() {
        return "google_" + UUID.randomUUID().toString().replace("-", "");
    }
}
