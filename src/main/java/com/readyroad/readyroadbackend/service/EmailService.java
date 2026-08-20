package com.readyroad.readyroadbackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Email Service
 *
 * Sends transactional emails (password reset, etc.) via SMTP.
 * All sends are asynchronous so the HTTP response is never blocked.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from:info@rijvia.be}")
    private String fromAddress;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    /**
     * Sends a password-reset email containing a one-time link.
     *
     * @param toEmail   recipient email address
     * @param token     the reset token (UUID)
     * @param fullName  recipient display name
     */
    @Async
    public void sendPasswordResetEmail(String toEmail, String token, String fullName) {
        String resetLink = buildResetLink(token);
        String subject   = "RijVia – Password Reset Request";
        String body      = buildResetEmailHtml(fullName, resetLink);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML
            mailSender.send(message);
            log.info("Password reset email sent to {}", toEmail);
        } catch (MessagingException | MailException e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage());
            // Don't rethrow — email failure must not expose user-existence info
        }
    }

    private String buildResetLink(String token) {
        String baseUrl = frontendUrl.endsWith("/")
                ? frontendUrl.substring(0, frontendUrl.length() - 1)
                : frontendUrl;
        return UriComponentsBuilder.fromUriString(baseUrl)
                .path("/reset-password")
                .queryParam("token", token)
                .build()
                .encode()
                .toUriString();
    }

    // ─── HTML Template ────────────────────────────────────────────────────────

    private String buildResetEmailHtml(String name, String resetLink) {
        String safeName = HtmlUtils.htmlEscape(name == null || name.isBlank() ? "RijVia user" : name);
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <title>Password Reset</title>
                </head>
                <body style="margin:0;padding:0;background:#f4f4f5;font-family:-apple-system,BlinkMacSystemFont,'Segoe UI',sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f5;padding:40px 16px;">
                    <tr><td align="center">
                      <table width="100%%" style="max-width:520px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,.08);">

                        <!-- Header -->
                        <tr>
                          <td style="background:linear-gradient(135deg,#2563eb,#1d4ed8);padding:32px;text-align:center;">
                            <div style="display:inline-block;width:56px;height:56px;background:rgba(255,255,255,.15);border-radius:14px;line-height:56px;font-size:28px;font-weight:900;color:#fff;">R</div>
                            <p style="margin:12px 0 0;color:#fff;font-size:20px;font-weight:700;">RijVia</p>
                          </td>
                        </tr>

                        <!-- Body -->
                        <tr>
                          <td style="padding:36px 32px;">
                            <h2 style="margin:0 0 8px;font-size:22px;color:#111827;">Password Reset Request</h2>
                            <p style="margin:0 0 24px;color:#6b7280;font-size:15px;line-height:1.6;">
                              Hello <strong>%s</strong>,<br/>
                              We received a request to reset your password. Click the button below within <strong>30 minutes</strong>.
                            </p>

                            <!-- CTA Button -->
                            <table cellpadding="0" cellspacing="0" style="margin:0 auto 28px;">
                              <tr>
                                <td style="background:#2563eb;border-radius:10px;">
                                  <a href="%s" style="display:inline-block;padding:14px 32px;color:#ffffff;font-size:15px;font-weight:600;text-decoration:none;border-radius:10px;">
                                    Reset My Password
                                  </a>
                                </td>
                              </tr>
                            </table>

                            <!-- Fallback link -->
                            <p style="margin:0 0 8px;color:#6b7280;font-size:13px;">Or paste this link in your browser:</p>
                            <p style="margin:0 0 28px;word-break:break-all;">
                              <a href="%s" style="color:#2563eb;font-size:13px;">%s</a>
                            </p>

                            <p style="margin:0;color:#9ca3af;font-size:13px;line-height:1.6;">
                              If you didn't request this, you can safely ignore this email — your password won't change.
                            </p>
                          </td>
                        </tr>

                        <!-- Footer -->
                        <tr>
                          <td style="background:#f9fafb;padding:20px 32px;text-align:center;border-top:1px solid #e5e7eb;">
                            <p style="margin:0;color:#9ca3af;font-size:12px;">
                              © %d RijVia · Belgian Driving License Exam Prep
                            </p>
                          </td>
                        </tr>

                      </table>
                    </td></tr>
                  </table>
                </body>
                </html>
                """.formatted(safeName, resetLink, resetLink, resetLink, java.time.Year.now().getValue());
    }
}
