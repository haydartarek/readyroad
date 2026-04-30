package com.readyroad.readyroadbackend.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt Password Hash Generator
 *
 * Small maintenance utility for development-only user creation.
 * Run this class when you need a one-off BCrypt hash for SQL or fixtures.
 *
 * Usage:
 * 1. Update the PASSWORD constant with your desired password
 * 2. Run this class: mvn exec:java
 * -Dexec.mainClass="com.readyroad.readyroadbackend.util.PasswordHashGenerator"
 * 3. Copy the generated hash to your SQL INSERT statement
 * 
 * @author ReadyRoad Team
 * @since 2026-01-29
 */
public class PasswordHashGenerator {

    // Change this to the password you want to hash
    private static final String PASSWORD = "Test123!";

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("=".repeat(70));
        System.out.println("BCrypt Password Hash Generator");
        System.out.println("=".repeat(70));
        System.out.println();
        System.out.println("Plain Password: " + PASSWORD);
        System.out.println();

        // Generate hash
        String hash = encoder.encode(PASSWORD);
        System.out.println("BCrypt Hash:");
        System.out.println(hash);
        System.out.println();

        // Verify the hash works
        boolean matches = encoder.matches(PASSWORD, hash);
        System.out.println("Verification: " + (matches ? "✅ PASS" : "❌ FAIL"));
        System.out.println();

        // SQL example
        System.out.println("SQL INSERT Example:");
        System.out.println("=".repeat(70));
        System.out.println("INSERT INTO users (");
        System.out.println("    username, email, password_hash, full_name,");
        System.out.println("    role, is_active, is_locked, created_at, updated_at");
        System.out.println(") VALUES (");
        System.out.println("    'testuser',");
        System.out.println("    'testuser@readyroad.be',");
        System.out.println("    '" + hash + "',");
        System.out.println("    'Test User',");
        System.out.println("    'USER',");
        System.out.println("    1,");
        System.out.println("    0,");
        System.out.println("    NOW(),");
        System.out.println("    NOW()");
        System.out.println(");");
        System.out.println("=".repeat(70));
    }
}
