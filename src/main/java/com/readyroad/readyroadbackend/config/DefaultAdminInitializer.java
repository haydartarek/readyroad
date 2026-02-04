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

            // Scenario: Do not recreate default admin user if it already exists
            if (userRepository.existsByUsername(adminUsername)) {
                log.info("ℹ️  Default admin user already exists - skipping creation");
                return;
            }

            // Scenario: Create default admin user if missing at startup
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setEmail("admin@readyroad.com");
            admin.setFullName("System Administrator");
            admin.setPasswordHash(passwordEncoder.encode("Admin123!"));
            admin.setRole(Role.ADMIN);
            admin.setIsActive(true);
            admin.setIsLocked(false);

            userRepository.save(admin);

            log.info("╔════════════════════════════════════════════════════════════╗");
            log.info("║        ✅ DEFAULT ADMIN USER CREATED                      ║");
            log.info("╚════════════════════════════════════════════════════════════╝");
            log.info("   Username: admin");
            log.info("   Password: Admin123!");
            log.info("   Email: admin@readyroad.com");
            log.info("   Role: ADMIN");
            log.info("");
            log.info("⚠️  IMPORTANT: Change this password immediately in production!");
            log.info("");

            // Verify the admin user was created successfully
            assert userRepository.existsByUsername(adminUsername) : "Admin user creation failed!";
            assert userRepository.findByUsername(adminUsername).get().getRole() == Role.ADMIN
                    : "Admin role not set correctly!";
            log.info("✅ Admin user verification passed");
        };
    }
}
