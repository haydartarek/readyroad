package com.readyroad.readyroadbackend.util;

import java.nio.charset.StandardCharsets;

/**
 * Utility to fix double-encoded Arabic text
 */
public class FixArabicEncoding {

    /**
     * Fix double-encoded UTF-8 text (stored as Latin1 then read as UTF-8)
     */
    public static String fixDoubleEncoding(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        try {
            // Check if text contains corrupted characters
            if (text.contains("Ù") || text.contains("Ø") || text.contains("â€")) {
                // Convert back to bytes using Latin1 (ISO-8859-1) then decode as UTF-8
                byte[] bytes = text.getBytes(StandardCharsets.ISO_8859_1);
                return new String(bytes, StandardCharsets.UTF_8);
            }
            return text;
        } catch (Exception e) {
            // If conversion fails, return original
            return text;
        }
    }
}
