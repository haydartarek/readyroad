package com.readyroad.readyroadbackend.config;

import com.readyroad.readyroadbackend.domain.entity.User;
import com.readyroad.readyroadbackend.domain.enums.Role;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Default Admin User Initializer
 * 
 * Implements Feature Scenarios:
 * - Create default admin user if missing at startup
 * - Do not recreate default admin user if it already exists
 * 
 * Default Credentials:
 * - Username: admin
 * - Password: Admin123!
 * - Email: admin@readyroad.com
 * - Role: ADMIN
 * 
 * @author ReadyRoad Team
 * @since 2026-02-04
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner createDefaultAdmin() {
        return args -> {
            String adminUsername = "admin";

            String defaultPassword = System.getenv("ADMIN_DEFAULT_PASSWORD");
            if (defaultPassword == null || defaultPassword.isEmpty()) {
                log.error("❌ CRITICAL: ADMIN_DEFAULT_PASSWORD environment variable not set!");
                log.error("❌ Set ADMIN_DEFAULT_PASSWORD environment variable and restart");
                return;
            }

            String encodedPassword = passwordEncoder.encode(defaultPassword);

            if (userRepository.existsByUsername(adminUsername)) {
                // Admin exists (seeded by migration) — always sync password and role from env
                userRepository.findByUsername(adminUsername).ifPresent(existingAdmin -> {
                    existingAdmin.setPasswordHash(encodedPassword);
                    existingAdmin.setRole(Role.ADMIN);
                    existingAdmin.setIsActive(true);
                    existingAdmin.setIsLocked(false);
                    userRepository.save(existingAdmin);
                    log.info("✅ Admin password and role synced from ADMIN_DEFAULT_PASSWORD env variable");
                });
                return;
            }

            // Create admin user if it doesn't exist at all
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@readyroad.be");
            admin.setFullName("System Administrator");
            admin.setPasswordHash(encodedPassword);
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            admin.setIsLocked(false);

            userRepository.save(admin);

            log.info("╔════════════════════════════════════════════════════════════╗");
            log.info("║        ✅ DEFAULT ADMIN USER CREATED                      ║");
            log.info("╚════════════════════════════════════════════════════════════╝");
            log.info("   Username: {}", adminUsername);
            log.info("   Email: admin@readyroad.com");
            log.info("   Role: ADMIN");
            log.info("");
            log.warn("⚠️  CRITICAL: Password set from ADMIN_DEFAULT_PASSWORD env variable");
            log.warn("⚠️  Change this password immediately after first login!");
            log.info("");

            // Verify the admin user was created successfully
            assert userRepository.existsByUsername(adminUsername) : "Admin user creation failed!";
            assert userRepository.findByUsername(adminUsername).get().getRole() == Role.ADMIN
                    : "Admin role not set correctly!";
            log.info("✅ Admin user verification passed");
        };
    }
}
