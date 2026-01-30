package com.readyroad.readyroadbackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hashes for database seeding.
 *
 * Usage: Run this class directly to generate a BCrypt hash for "Test123!"
 * Then use the generated hash in SQL INSERT statements.
 */
public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "Test123!";
        String hash = encoder.encode(password);

        System.out.println("============================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("============================");
        System.out.println();
        System.out.println("SQL INSERT:");
        System.out.println("INSERT INTO users (username, email, full_name, password_hash, role, is_active, is_locked)");
        System.out.println("VALUES ('testuser', 'testuser@readyroad.be', 'Test User', '" + hash + "', 'USER', 1, 0);");
        System.out.println();

        // Verify the hash works
        boolean matches = encoder.matches(password, hash);
        System.out.println("Verification: " + (matches ? "PASS" : "FAIL"));
    }
}
