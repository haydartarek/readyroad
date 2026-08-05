package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import com.readyroad.readyroadbackend.dto.AdminCreateUserRequest;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminUserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BackendMessageService messages;

    @Transactional
    public User createUser(AdminCreateUserRequest request, String actor) {
        String username = request.username().trim();
        String email = request.email().trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalStateException(messages.get("auth.username_exists"));
        }
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException(messages.get("auth.email_exists"));
        }

        Role role = Role.valueOf(request.role());
        if (role == Role.STUDENT) {
            throw new IllegalArgumentException(messages.get("admin.user.invalid_role"));
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setFullName((request.firstName().trim() + " " + request.lastName().trim()).replaceAll("\\s+", " "));
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setPreferredLanguage(request.preferredLanguage());
        user.setRole(role);
        user.setIsActive(request.isActive());
        user.setIsLocked(false);
        user.setEmailVerified(request.emailVerified());

        try {
            User saved = userRepository.save(user);
            auditAfterCommit(actor, saved);
            return saved;
        } catch (DataIntegrityViolationException ex) {
            throw new IllegalStateException(messages.get("admin.user.duplicate"), ex);
        }
    }

    private void auditAfterCommit(String actor, User user) {
        Runnable writeAudit = () -> log.info(
                "AUDIT admin_user_create actor={} userId={} username={} role={} active={} emailVerified={}",
                actor, user.getId(), user.getUsername(), user.getRole(), user.getIsActive(), user.getEmailVerified());
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    writeAudit.run();
                }
            });
        } else {
            writeAudit.run();
        }
    }
}
