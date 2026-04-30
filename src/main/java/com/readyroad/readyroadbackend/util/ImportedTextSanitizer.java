package com.readyroad.readyroadbackend.util;

import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public final class ImportedTextSanitizer {

    private static final Charset WINDOWS_1252 = Charset.forName("windows-1252");
    private static final List<Charset> SOURCE_ENCODINGS = List.of(
            StandardCharsets.ISO_8859_1,
            WINDOWS_1252);
    private static final Map<Character, Integer> CP1252_REVERSE_BYTES = Map.ofEntries(
            Map.entry('\u20AC', 0x80),
            Map.entry('\u201A', 0x82),
            Map.entry('\u0192', 0x83),
            Map.entry('\u201E', 0x84),
            Map.entry('\u2026', 0x85),
            Map.entry('\u2020', 0x86),
            Map.entry('\u2021', 0x87),
            Map.entry('\u02C6', 0x88),
            Map.entry('\u2030', 0x89),
            Map.entry('\u0160', 0x8A),
            Map.entry('\u2039', 0x8B),
            Map.entry('\u0152', 0x8C),
            Map.entry('\u017D', 0x8E),
            Map.entry('\u2018', 0x91),
            Map.entry('\u2019', 0x92),
            Map.entry('\u201C', 0x93),
            Map.entry('\u201D', 0x94),
            Map.entry('\u2022', 0x95),
            Map.entry('\u2013', 0x96),
            Map.entry('\u2014', 0x97),
            Map.entry('\u02DC', 0x98),
            Map.entry('\u2122', 0x99),
            Map.entry('\u0161', 0x9A),
            Map.entry('\u203A', 0x9B),
            Map.entry('\u0153', 0x9C),
            Map.entry('\u017E', 0x9E),
            Map.entry('\u0178', 0x9F));

    private ImportedTextSanitizer() {
    }

    public static String sanitize(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty() || !looksLikeMojibake(trimmed)) {
            return trimmed;
        }

        String best = trimmed;
        String decodedMixed = decodeMixedLatinMojibake(trimmed);
        if (decodedMixed != null && !decodedMixed.isEmpty() && !containsReplacementCharacter(decodedMixed)
                && isBetterCandidate(decodedMixed, best)) {
            best = decodedMixed;
        }

        for (Charset sourceEncoding : SOURCE_ENCODINGS) {
            String decoded = new String(trimmed.getBytes(sourceEncoding), StandardCharsets.UTF_8).trim();
            if (decoded.isEmpty() || containsReplacementCharacter(decoded)) {
                continue;
            }
            if (isBetterCandidate(decoded, best)) {
                best = decoded;
            }
        }

        return best;
    }

    public static boolean requiresRepair(String value) {
        if (value == null) {
            return false;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        return !trimmed.equals(sanitize(trimmed));
    }

    private static boolean looksLikeMojibake(String value) {
        return value.indexOf('Ã') >= 0
                || value.indexOf('Ø') >= 0
                || value.indexOf('Ù') >= 0
                || value.indexOf('Â') >= 0
                || value.indexOf('Ð') >= 0
                || value.indexOf('Ñ') >= 0;
    }

    private static boolean containsReplacementCharacter(String value) {
        return value.indexOf('\uFFFD') >= 0;
    }

    private static int mojibakeScore(String value) {
        int score = 0;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == 'Ã' || ch == 'Ø' || ch == 'Ù' || ch == 'Â' || ch == 'Ð' || ch == 'Ñ' || ch == '\uFFFD') {
                score++;
            }
        }
        return score;
    }

    private static boolean isBetterCandidate(String candidate, String baseline) {
        int candidateScore = mojibakeScore(candidate);
        int baselineScore = mojibakeScore(baseline);
        if (candidateScore != baselineScore) {
            return candidateScore < baselineScore;
        }
        return readabilityScore(candidate) > readabilityScore(baseline);
    }

    private static int readabilityScore(String value) {
        int score = 0;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (Character.isLetterOrDigit(ch)) {
                score++;
            }
            Character.UnicodeBlock block = Character.UnicodeBlock.of(ch);
            if (block == Character.UnicodeBlock.ARABIC
                    || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_A
                    || block == Character.UnicodeBlock.ARABIC_PRESENTATION_FORMS_B
                    || block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT
                    || block == Character.UnicodeBlock.LATIN_EXTENDED_A
                    || block == Character.UnicodeBlock.LATIN_EXTENDED_B) {
                score++;
            }
        }
        return score;
    }

    private static String decodeMixedLatinMojibake(String value) {
        ByteArrayOutputStream output = new ByteArrayOutputStream(value.length());
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch <= 0xFF) {
                output.write((byte) ch);
                continue;
            }

            Integer mapped = CP1252_REVERSE_BYTES.get(ch);
            if (mapped == null) {
                return null;
            }
            output.write(mapped);
        }
        return new String(output.toByteArray(), StandardCharsets.UTF_8).trim();
    }
}
