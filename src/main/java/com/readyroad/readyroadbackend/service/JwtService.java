package com.readyroad.readyroadbackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT Service
 *
 * Handles JWT token generation, validation, and extraction.
 *
 * Features:
 * - Generate JWT tokens for authenticated users
 * - Validate token signatures and expiration
 * - Extract user information from tokens
 *
 * @author ReadyRoad Team
 * @since 2026-01-18
 */
@Service
@Slf4j
public class JwtService {

    // ✅ تغيير من jwt.secret إلى jwt.secret-key
    @Value("${jwt.secret-key:}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    @Value("${jwt.issuer:readyroad-backend}")
    private String issuer;

    /**
     * Validate JWT configuration on startup
     */
    @PostConstruct
    public void init() {
        log.info("=== JWT Service Initialization ===");
        log.info("Secret Key Length: {} characters", secret != null ? secret.length() : 0);
        log.info("JWT Expiration: {} ms ({} hours)", expiration, expiration / 3600000.0);
        log.info("JWT Issuer: {}", issuer);

        if (secret == null || secret.isEmpty() || secret.equals("not-used-in-dev-mode")) {
            log.error("❌ CRITICAL: JWT secret key is not configured properly!");
            throw new IllegalStateException("JWT secret key must be configured in application-dev.yml");
        }

        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            if (keyBytes.length < 32) {
                log.error("❌ Secret key is too short: {} bytes (minimum 32 bytes required)", keyBytes.length);
                throw new IllegalStateException("JWT secret key must be at least 256 bits (32 bytes)");
            }
            log.info("✅ JWT secret key is valid: {} bytes ({} bits)", keyBytes.length, keyBytes.length * 8);
        } catch (IllegalArgumentException e) {
            log.error("❌ Invalid Base64 format for JWT secret key", e);
            throw new IllegalStateException("JWT secret key must be valid Base64 encoded string", e);
        }
    }

    /**
     * Extract username from JWT token
     *
     * @param token JWT token
     * @return Username
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from JWT token
     *
     * @param token JWT token
     * @return Expiration date
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from JWT token
     *
     * @param token          JWT token
     * @param claimsResolver Function to extract claim
     * @param <T>            Claim type
     * @return Extracted claim
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from JWT token
     *
     * @param token JWT token
     * @return All claims
     */
    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Check if token is expired
     *
     * @param token JWT token
     * @return true if expired, false otherwise
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Validate JWT token against user details
     *
     * @param token       JWT token
     * @param userDetails User details
     * @return true if valid, false otherwise
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Generate JWT token for user
     *
     * @param username Username
     * @return JWT token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * Generate JWT token with custom claims
     *
     * @param extraClaims Extra claims to include
     * @param username    Username
     * @return JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, String username) {
        return createToken(extraClaims, username);
    }

    /**
     * Create JWT token
     *
     * @param claims   Claims to include
     * @param username Username (subject)
     * @return JWT token
     */
    private String createToken(Map<String, Object> claims, String username) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuer(issuer)
                .issuedAt(new Date(now))
                .expiration(new Date(now + expiration))
                .signWith(getSignKey())
                .compact();
    }

    /**
     * Get signing key from secret
     *
     * @return Signing key
     */
    private SecretKey getSignKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(secret);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            log.error("Error decoding JWT secret key", e);
            throw new RuntimeException("Invalid JWT secret key format", e);
        }
    }
}
