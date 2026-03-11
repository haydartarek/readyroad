package com.readyroad.readyroadbackend.util;

import java.util.regex.Pattern;

/**
 * Detects placeholder or corrupted translation text in quiz answer options.
 *
 * <p>
 * A large number of options imported during the initial content migration
 * contain placeholder values (e.g. "Option A", "Optie B") or Arabic text that
 * was stored as repeated question marks ("??????") due to an encoding failure.
 * These values must never be exposed to learners.
 *
 * <h3>Detected patterns</h3>
 * <ul>
 * <li>{@code ^Option [A-Z]$} — English / French placeholder
 * (case-insensitive)</li>
 * <li>{@code ^Optie [A-Z]$} — Dutch placeholder (case-insensitive)</li>
 * <li>Two or more consecutive '?' characters — corrupted Arabic encoding</li>
 * </ul>
 */
public final class PlaceholderDetector {

    private PlaceholderDetector() {
        /* utility – no instances */ }

    /** Matches: "Option A", "option b", "OPTION Z", etc. (EN / FR placeholders). */
    private static final Pattern OPTION_EN_FR = Pattern.compile("^option\\s+[a-z]$", Pattern.CASE_INSENSITIVE);

    /** Matches: "Optie A", "optie b", "OPTIE Z", etc. (NL placeholders). */
    private static final Pattern OPTIE_NL = Pattern.compile("^optie\\s+[a-z]$", Pattern.CASE_INSENSITIVE);

    /**
     * Matches corrupted Arabic text stored as sequences of '?' characters.
     * Triggered by two or more consecutive question marks anywhere in the value.
     * Examples: "??????", "?????? ?", "???? ?".
     */
    private static final Pattern CORRUPT_QUESTION_MARKS = Pattern.compile("\\?{2,}");

    /**
     * Returns {@code true} if the given text is a recognised placeholder or
     * corrupted translation value that must not be shown to users.
     *
     * <p>
     * A {@code null} or blank text is also considered a placeholder because
     * it carries no meaningful content.
     *
     * @param text the option text to test (single language)
     * @return {@code true} if the text is invalid / placeholder
     */
    public static boolean isPlaceholder(String text) {
        if (text == null || text.isBlank()) {
            return true;
        }
        String trimmed = text.trim();
        return OPTION_EN_FR.matcher(trimmed).matches()
                || OPTIE_NL.matcher(trimmed).matches()
                || CORRUPT_QUESTION_MARKS.matcher(trimmed).find();
    }

    /**
     * Returns {@code true} if ANY of the provided multilingual text values is a
     * placeholder. A {@code null} entry counts as a placeholder.
     *
     * @param texts one or more language variants of the same option text
     * @return {@code true} if at least one variant is a placeholder
     */
    public static boolean hasPlaceholder(String... texts) {
        for (String text : texts) {
            if (isPlaceholder(text)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if ANY non-blank text in the provided array is a
     * placeholder. {@code null} and blank values are silently ignored — they are
     * treated as "not yet provided" rather than corrupted.
     *
     * <p>
     * Use this variant when validating admin / import payloads where some
     * language fields may legitimately be absent.
     *
     * @param texts one or more language variants (may contain nulls)
     * @return {@code true} if at least one non-blank value is a placeholder
     */
    public static boolean hasPlaceholderNonBlank(String... texts) {
        for (String text : texts) {
            if (text != null && !text.isBlank() && isPlaceholder(text)) {
                return true;
            }
        }
        return false;
    }
}
