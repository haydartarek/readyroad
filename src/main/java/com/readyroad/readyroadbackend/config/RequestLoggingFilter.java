package com.readyroad.readyroadbackend.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Optional request logging filter for deep troubleshooting.
 *
 * Disabled by default to avoid noisy logs in normal development and production.
 * Enable it only when actively debugging request flow with:
 * readyroad.request-logging.enabled=true
 *
 * @author ReadyRoad Team
 * @since 2026-01-23
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "readyroad.request-logging.enabled", havingValue = "true")
@Order(1)  // Execute first
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();
        String queryString = httpRequest.getQueryString();

        log.info("========================================");
        log.info("INCOMING REQUEST");
        log.info("========================================");
        log.info("Method: {}", method);
        log.info("URI: {}", uri);
        if (queryString != null) {
            log.info("Query: {}", queryString);
        }
        log.info("Remote Address: {}", httpRequest.getRemoteAddr());

        // Log headers
        log.info("----------------------------------------");
        log.info("Request Headers:");
        Enumeration<String> headerNames = httpRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = httpRequest.getHeader(headerName);

            // Don't log the full Authorization header for security
            if ("Authorization".equalsIgnoreCase(headerName) && headerValue.startsWith("Bearer ")) {
                log.info("  {}: Bearer [TOKEN_HIDDEN]", headerName);
            } else {
                log.info("  {}: {}", headerName, headerValue);
            }
        }
        log.info("========================================");

        // Continue the filter chain
        chain.doFilter(request, response);
    }
}
