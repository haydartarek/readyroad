package com.readyroad.readyroadbackend.config;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
@Profile("secure")
public class SecurityConfigSecure {


    private final JwtAuthenticationFilter jwtAuthFilter;


    // ⭐ استخدام default values لتجنب مشاكل placeholder
    @Value("${app.cors.allowed-origins:http://localhost:3000,http://localhost:3001,http://localhost:8890}")
    private String allowedOriginsString;


    @Value("${app.cors.max-age:3600}")
    private Long corsMaxAge;


    // Constants
    private static final String API_AUTH = "/api/auth/**";
    private static final String API_V1_AUTH = "/api/v1/auth/**";
    private static final String API_CATEGORIES = "/api/categories/**";
    private static final String API_V1_CATEGORIES = "/api/v1/categories/**";
    private static final String API_LESSONS = "/api/lessons/**";
    private static final String API_V1_LESSONS = "/api/v1/lessons/**";
    private static final String API_TRAFFIC_SIGNS = "/api/traffic-signs/**";
    private static final String API_V1_TRAFFIC_SIGNS = "/api/v1/traffic-signs/**";


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("╔═══════════════════════════════════════════════════════════╗");
        log.info("║   Configuring Security Filter Chain with RBAC            ║");
        log.info("╚═══════════════════════════════════════════════════════════╝");


        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                
                // ⭐ إضافة /api/health كـ public endpoint
                .requestMatchers("/api/health").permitAll()
                
                // PUBLIC AUTH
                .requestMatchers(
                    "/api/auth/login", 
                    "/api/auth/register", 
                    "/api/auth/refresh",
                    "/api/auth/health",
                    "/api/v1/auth/login",
                    "/api/v1/auth/register",
                    "/api/v1/auth/refresh-token"
                ).permitAll()


                .requestMatchers(HttpMethod.POST, API_AUTH).permitAll()
                .requestMatchers(HttpMethod.POST, API_V1_AUTH).permitAll()
                .requestMatchers("/api/auth/me", "/api/v1/auth/me").authenticated()


                // MONITORING
                .requestMatchers(
                    "/actuator/health",
                    "/actuator/info",
                    "/actuator/metrics",
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()


                // PUBLIC READ
                .requestMatchers(HttpMethod.GET, API_CATEGORIES, API_V1_CATEGORIES).permitAll()
                .requestMatchers(HttpMethod.GET, API_LESSONS, API_V1_LESSONS).permitAll()
                .requestMatchers(HttpMethod.GET, API_TRAFFIC_SIGNS, API_V1_TRAFFIC_SIGNS).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/quiz-questions/**", "/api/v1/quiz-questions/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/search", "/api/v1/search").permitAll()


                // ADMIN ONLY
                .requestMatchers("/api/admin/**", "/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/data-import/**", "/api/v1/data-import/**").hasRole("ADMIN")


                // ADMIN WRITE
                .requestMatchers(HttpMethod.POST, API_CATEGORIES, API_V1_CATEGORIES).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, API_CATEGORIES, API_V1_CATEGORIES).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, API_CATEGORIES, API_V1_CATEGORIES).hasRole("ADMIN")


                .requestMatchers(HttpMethod.POST, API_LESSONS, API_V1_LESSONS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, API_LESSONS, API_V1_LESSONS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, API_LESSONS, API_V1_LESSONS).hasRole("ADMIN")


                .requestMatchers(HttpMethod.POST, API_TRAFFIC_SIGNS, API_V1_TRAFFIC_SIGNS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, API_TRAFFIC_SIGNS, API_V1_TRAFFIC_SIGNS).hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, API_TRAFFIC_SIGNS, API_V1_TRAFFIC_SIGNS).hasRole("ADMIN")


                .requestMatchers(HttpMethod.POST, "/api/quiz-questions/**", "/api/v1/quiz-questions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/quiz-questions/**", "/api/v1/quiz-questions/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/quiz-questions/**", "/api/v1/quiz-questions/**").hasRole("ADMIN")


                // MODERATION
                .requestMatchers("/api/moderation/**", "/api/v1/moderation/**").hasAnyRole("MODERATOR", "ADMIN")


                // STATIC
                .requestMatchers("/images/**", "/static/**", "/public/**", "/favicon.ico", "/robots.txt", "/error").permitAll()


                // USER PROFILE
                .requestMatchers("/api/users/me/**", "/api/v1/users/me/**").authenticated()


                // DEFAULT
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )


            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    log.warn("⚠️  Authentication failed | URI: {} | Reason: {}", 
                        request.getRequestURI(), authException.getMessage());
                    
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(String.format(
                        "{\"error\":\"Unauthorized\",\"message\":\"Authentication required\",\"path\":\"%s\",\"timestamp\":%d}",
                        request.getRequestURI(), System.currentTimeMillis()
                    ));
                })
                
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.warn("🚫 Access denied | URI: {} | Reason: {}", 
                        request.getRequestURI(), accessDeniedException.getMessage());
                    
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(String.format(
                        "{\"error\":\"Access Denied\",\"message\":\"Insufficient permissions\",\"path\":\"%s\",\"timestamp\":%d}",
                        request.getRequestURI(), System.currentTimeMillis()
                    ));
                })
            )


            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);


        log.info("✅ Security Filter Chain configured successfully");
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // ⭐ تحويل comma-separated string إلى list
        List<String> allowedOrigins = Arrays.asList(allowedOriginsString.split(","));
        
        log.info("🌍 Configuring CORS | Allowed origins: {}", allowedOrigins);


        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        
        configuration.setAllowedMethods(Arrays.asList(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.PUT.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.OPTIONS.name(),
            HttpMethod.PATCH.name(),
            HttpMethod.HEAD.name()
        ));
        
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(Arrays.asList(
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Authorization"
        ));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(corsMaxAge);


        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        log.info("✅ CORS configured successfully");
        return source;
    }
}
