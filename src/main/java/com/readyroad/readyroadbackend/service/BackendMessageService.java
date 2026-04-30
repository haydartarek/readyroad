package com.readyroad.readyroadbackend.service;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

/**
 * Lightweight backend i18n facade for API and service-layer user-facing copy.
 *
 * Uses Spring's default message bundle resolution (messages*.properties) and
 * normalizes unsupported locales back to English.
 */
@Service
@RequiredArgsConstructor
public class BackendMessageService {

    private final MessageSource messageSource;

    public String get(String key, Object... args) {
        return messageSource.getMessage(key, args, key, resolveLocale());
    }

    private Locale resolveLocale() {
        Locale locale = LocaleContextHolder.getLocale();
        if (locale == null) {
            return Locale.ENGLISH;
        }

        return switch (locale.getLanguage()) {
            case "ar" -> Locale.forLanguageTag("ar");
            case "fr" -> Locale.FRENCH;
            case "nl" -> Locale.forLanguageTag("nl");
            default -> Locale.ENGLISH;
        };
    }
}
