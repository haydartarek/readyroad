package com.readyroad.readyroadbackend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.readyroad.readyroadbackend.domain.entity.Notification;
import com.readyroad.readyroadbackend.domain.repository.UserRepository;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.Security;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import nl.martijndwars.webpush.AbstractPushService;
import nl.martijndwars.webpush.Encoding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class LearningNotificationTransport {
    private final JdbcTemplate jdbc;
    private final UserRepository users;
    private final JavaMailSender mail;
    private final ObjectMapper json;
    private final AdminLearningStore learning;
    private final JsonNode translations;
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NEVER).build();
    @Value("${readyroad.notifications.email-enabled:false}") private boolean emailEnabled;
    @Value("${readyroad.notifications.push-public-key:}") private String publicKey;
    @Value("${readyroad.notifications.push-private-key:}") private String privateKey;
    @Value("${readyroad.notifications.push-subject:mailto:info@rijvia.be}") private String subject;
    @Value("${app.mail.from:info@rijvia.be}") private String from;
    @Value("${app.frontend.url:http://localhost:3000}") private String siteUrl;

    public LearningNotificationTransport(JdbcTemplate jdbc, UserRepository users, JavaMailSender mail,
            ObjectMapper json, AdminLearningStore learning) throws java.io.IOException {
        this.jdbc = jdbc; this.users = users; this.mail = mail; this.json = json;
        this.learning = learning;
        try (var input = getClass().getResourceAsStream("/learning-notifications.json")) {
            if (input == null) throw new java.io.IOException("Learning notification translations missing");
            this.translations = json.readTree(input);
        }
    }

    public boolean emailAvailable() { return emailEnabled; }
    public boolean pushAvailable() { return !publicKey.isBlank() && !privateKey.isBlank(); }
    public String publicKey() { return pushAvailable() ? publicKey : ""; }

    public static void validateSubscriptionPublicKey(String value) {
        try {
            if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
            nl.martijndwars.webpush.Utils.loadPublicKey(value);
        } catch (Exception ex) { throw new IllegalArgumentException("Invalid subscription public key"); }
    }

    // False means deliberately cancelled (opt-out, removed subscription, or inactive account).
    boolean send(String channel, UUID subscriptionId, Notification notification) {
        var user = users.findById(notification.getUserId()).orElse(null);
        if (user == null || !Boolean.TRUE.equals(user.getIsActive())
                || user.getRole() != com.readyroad.readyroadbackend.domain.enums.Role.USER) return false;
        if (notification.getType() == com.readyroad.readyroadbackend.domain.entity.NotificationType.STUDY_REMINDER) {
            var profile = learning.findStudent(user.getId());
            var lastActivity = profile != null && profile.lastActiveAt() != null ? profile.lastActiveAt() : user.getCreatedAt();
            if (lastActivity != null && lastActivity.isAfter(
                    java.time.LocalDateTime.now(java.time.ZoneOffset.UTC).minusHours(24))) return false;
        }
        String locale = Set.of("ar", "nl", "fr", "en").contains(String.valueOf(user.getPreferredLanguage()))
                ? user.getPreferredLanguage() : "en";
        String link = notification.getLink();
        if (link == null || !link.startsWith("/") || link.startsWith("//") || link.contains("\\")) link = "/dashboard";
        String url = siteUrl.replaceAll("/+$", "") + (locale.equals("en") ? "" : "/" + locale) + link;
        String body = localizedMessage(notification, locale);
        if ("EMAIL".equals(channel)) {
            boolean optedIn = Boolean.TRUE.equals(jdbc.queryForObject("""
                    SELECT COALESCE((SELECT email_enabled FROM learning_notification_preferences WHERE user_id = ?), TRUE)
                    """, Boolean.class, user.getId()));
            if (!optedIn) return false;
            if (!emailAvailable()) throw new IllegalStateException("Email delivery not configured");
            var message = new SimpleMailMessage();
            message.setFrom(from); message.setTo(user.getEmail()); message.setSubject("RijVia");
            message.setText(body + "\n\n" + url);
            mail.send(message);
            return true;
        }
        var subscriptions = jdbc.queryForList("""
                SELECT endpoint, p256dh, auth_secret FROM learning_push_subscriptions
                WHERE id = ? AND user_id = ? AND active
                """, subscriptionId, user.getId());
        if (subscriptions.isEmpty()) return false;
        if (!pushAvailable()) throw new IllegalStateException("Web Push not configured");
        var subscription = subscriptions.getFirst();
        String endpoint = (String) subscription.get("endpoint");
        validateEndpoint(endpoint);
        try {
            if (Security.getProvider("BC") == null) Security.addProvider(new BouncyCastleProvider());
            var payload = json.writeValueAsBytes(Map.of("title", "RijVia", "body", body, "url", url,
                    "tag", "learning-" + notification.getId()));
            var push = new nl.martijndwars.webpush.Notification(endpoint,
                    (String) subscription.get("p256dh"), (String) subscription.get("auth_secret"), payload);
            var prepared = new PreparedPush(publicKey, privateKey, subject).prepare(push);
            validateEndpoint(prepared.getUrl());
            var request = HttpRequest.newBuilder(URI.create(prepared.getUrl())).timeout(Duration.ofSeconds(15))
                    .POST(HttpRequest.BodyPublishers.ofByteArray(prepared.getBody()));
            prepared.getHeaders().forEach(request::header);
            var response = http.send(request.build(), HttpResponse.BodyHandlers.discarding());
            int status = response.statusCode();
            if (status == 404 || status == 410) {
                jdbc.update("UPDATE learning_push_subscriptions SET active = FALSE WHERE id = ?", subscriptionId);
                return false;
            }
            if (status < 200 || status >= 300) throw new PushDeliveryException(status,
                    retryAfterSeconds(response.headers().firstValue("Retry-After").orElse("")));
            return true;
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Web Push interrupted", ex);
        } catch (PushDeliveryException ex) { throw ex;
        } catch (Exception ex) {
            throw new IllegalStateException("Web Push delivery failed", ex);
        }
    }

    String localizedMessage(Notification notification, String locale) {
        String key = notification.getMessageKey();
        String text = key == null ? notification.getMessage()
                : translations.path(locale).path(key).asText(notification.getMessage());
        if (notification.getMessageParams() == null) return text;
        try {
            var params = json.readTree(notification.getMessageParams());
            for (String name : new String[] {"category", "lesson"}) {
                String suffix = locale.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + locale.substring(1);
                text = text.replace("{" + name + "}", params.path(name + suffix).asText(params.path(name).asText("")));
            }
            var fields = params.fields();
            while (fields.hasNext()) {
                var field = fields.next();
                text = text.replace("{" + field.getKey() + "}", field.getValue().asText());
            }
            return text;
        } catch (Exception ex) { throw new IllegalArgumentException("Invalid notification parameters", ex); }
    }

    public static void validateEndpoint(String endpoint) {
        URI uri;
        try { uri = URI.create(endpoint); } catch (RuntimeException ex) { throw new IllegalArgumentException("Invalid push endpoint"); }
        String host = uri.getHost();
        boolean allowed = host != null && (host.equals("fcm.googleapis.com") || host.equals("web.push.apple.com")
                || host.equals("updates.push.services.mozilla.com") || host.endsWith(".notify.windows.com"));
        if (!allowed || !"https".equals(uri.getScheme()) || uri.getUserInfo() != null
                || uri.getFragment() != null || (uri.getPort() != -1 && uri.getPort() != 443))
            throw new IllegalArgumentException("Unsupported push endpoint");
    }

    static final class PushDeliveryException extends RuntimeException {
        final int status;
        final long retryAfterSeconds;
        PushDeliveryException(int status, long retryAfterSeconds) {
            super("Web Push HTTP " + status); this.status = status; this.retryAfterSeconds = retryAfterSeconds;
        }
    }

    static long retryAfterSeconds(String value) {
        try { return Math.min(86400, Math.max(0, Long.parseLong(value)));
        } catch (NumberFormatException ex) {
            try { return Math.min(86400, Math.max(0, Duration.between(java.time.Instant.now(),
                    java.time.ZonedDateTime.parse(value, java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME).toInstant()).toSeconds()));
            } catch (java.time.DateTimeException invalid) { return 0; }
        }
    }

    static final class PreparedPush extends AbstractPushService<PreparedPush> {
        PreparedPush(String publicKey, String privateKey, String subject) throws Exception {
            super(publicKey, privateKey, subject);
        }
        nl.martijndwars.webpush.HttpRequest prepare(nl.martijndwars.webpush.Notification notification) throws Exception {
            return prepareRequest(notification, Encoding.AES128GCM);
        }
    }
}
