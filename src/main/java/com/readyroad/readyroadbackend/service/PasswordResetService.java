package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.PasswordResetToken;
import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.repository.PasswordResetTokenRepository;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Password Reset Service
 *
 * Handles the complete forgot-password / reset-password flow:
 *  1. forgotPassword(email)  — generate token, send email
 *  2. resetPassword(token, newPassword) — validate token, update password
 *
 * Security notes:
 *  - forgotPassword always returns success (prevents user-enumeration)
 *  - Tokens expire after 30 minutes and are single-use
 *  - Old tokens for a user are deleted before issuing a new one
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_EXPIRY_MINUTES = 30;

    private final UserRepository                userRepository;
    private final PasswordResetTokenRepository  tokenRepository;
    private final EmailService                  emailService;
    private final PasswordEncoder               passwordEncoder;
    private final BackendMessageService         messages;

    /**
     * Step 1 — Request a password reset.
     *
     * If the email is registered we create a token and email the link.
     * If the email is unknown we do nothing but return the same success message
     * to prevent user-enumeration attacks.
     */
    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            // Delete any previous (unused / expired) tokens for this user
            tokenRepository.deleteAllByUserId(user.getId());

            // Create a new token
            PasswordResetToken prt = new PasswordResetToken();
            prt.setToken(UUID.randomUUID().toString());
            prt.setUser(user);
            prt.setExpiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES));
            prt.setUsed(false);
            tokenRepository.save(prt);

            // Send email asynchronously
            emailService.sendPasswordResetEmail(email, prt.getToken(), user.getFullName());
            log.info("Password reset token issued for userId={}", user.getId());
        });
    }

    /**
     * Step 2 — Apply the new password.
     *
     * @throws IllegalArgumentException with a user-safe message on any failure
     */
    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken prt = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException(messages.get("auth.reset_password.invalid_link")));

        if (prt.getUsed()) {
            throw new IllegalArgumentException(messages.get("auth.reset_password.already_used"));
        }
        if (prt.isExpired()) {
            throw new IllegalArgumentException(messages.get("auth.reset_password.expired"));
        }

        // Update password
        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark token as used (don't delete — keeps audit trail briefly)
        prt.setUsed(true);
        tokenRepository.save(prt);

        log.info("Password reset successfully for userId={}", user.getId());
    }

    /**
     * Scheduled cleanup — runs every hour to remove expired / used tokens.
     */
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredAndUsed(LocalDateTime.now());
        log.debug("Cleaned up expired/used password reset tokens");
    }
}
