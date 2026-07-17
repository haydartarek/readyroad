package com.readyroad.readyroadbackend.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EmailServiceTest {

    private JavaMailSender mailSender;
    private EmailService emailService;
    private MimeMessage message;

    @BeforeEach
    void setUp() {
        mailSender = mock(JavaMailSender.class);
        emailService = new EmailService(mailSender);
        ReflectionTestUtils.setField(emailService, "fromAddress", "noreply@readyroad.be");
        ReflectionTestUtils.setField(emailService, "frontendUrl", "https://readyroad.example/");
        message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
    }

    @Test
    void buildsProductionResetLinkAndEscapesDisplayName() {
        emailService.sendPasswordResetEmail(
                "driver@example.com",
                "token-with-special+value",
                "<strong>Driver</strong>");

        verify(mailSender).send(message);
        String resetLink = ReflectionTestUtils.invokeMethod(
                emailService,
                "buildResetLink",
                "token-with-special+value");
        String html = ReflectionTestUtils.invokeMethod(
                emailService,
                "buildResetEmailHtml",
                "<strong>Driver</strong>",
                resetLink);
        assertThat(html)
                .contains("https://readyroad.example/reset-password?token=token-with-special+value")
                .contains("&lt;strong&gt;Driver&lt;/strong&gt;")
                .doesNotContain("<strong><strong>Driver</strong></strong>");
    }

    @Test
    void containsMailAuthenticationFailureInsideAsyncBoundary() {
        doThrow(new MailAuthenticationException("SMTP credentials rejected"))
                .when(mailSender).send(message);

        assertThatCode(() -> emailService.sendPasswordResetEmail(
                "driver@example.com",
                "reset-token",
                "Driver"))
                .doesNotThrowAnyException();
    }
}
