package com.readyroad.readyroadbackend.marketing.analytics;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.task.MarketingTaskExecutionException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoogleServiceAccountCredentials {

    private static final List<String> READ_ONLY_SCOPES = List.of(
            "https://www.googleapis.com/auth/analytics.readonly",
            "https://www.googleapis.com/auth/webmasters.readonly");

    private final MarketingProperties properties;
    private volatile GoogleCredentials credentials;

    public boolean isConfigured() {
        String configuredPath = properties.getAnalytics().getServiceAccountFile();
        return configuredPath != null
                && !configuredPath.isBlank()
                && Files.isRegularFile(Path.of(configuredPath));
    }

    public String accessToken() {
        GoogleCredentials current = credentials();
        try {
            current.refreshIfExpired();
            if (current.getAccessToken() == null) {
                current.refresh();
            }
            return current.getAccessToken().getTokenValue();
        } catch (IOException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_CREDENTIALS", "Google read-only credentials could not be refreshed");
        }
    }

    private GoogleCredentials credentials() {
        GoogleCredentials current = credentials;
        if (current != null) {
            return current;
        }
        synchronized (this) {
            if (credentials == null) {
                credentials = load();
            }
            return credentials;
        }
    }

    private GoogleCredentials load() {
        if (!isConfigured()) {
            throw new MarketingTaskExecutionException(
                    "INVALID_CREDENTIALS", "Google read-only Service Account is not configured");
        }
        Path path = Path.of(properties.getAnalytics().getServiceAccountFile());
        try (InputStream input = Files.newInputStream(path)) {
            return ServiceAccountCredentials.fromStream(input).createScoped(READ_ONLY_SCOPES);
        } catch (IOException | RuntimeException error) {
            throw new MarketingTaskExecutionException(
                    "INVALID_CREDENTIALS", "Google read-only Service Account file is invalid");
        }
    }
}
