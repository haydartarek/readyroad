package com.readyroad.readyroadbackend.util;

/**
 * Normalizes typographic (smart) quote characters to their plain ASCII
 * equivalents before persisting to the database.
 *
 * <p>Smart quotes (U+2018/U+2019 for single, U+201C/U+201D for double) are
 * introduced when content is typed or pasted from word processors (Microsoft
 * Word, Google Docs) or websites.  MySQL treats them as string data, so an
 * unescaped U+2019 inside a SQL single-quoted literal breaks the statement at
 * runtime (as happened with sign E9j whose Dutch text contained {@code auto\u2019s}).
 *
 * <p>This normalizer is called automatically via the JPA
 * {@code @PrePersist} / {@code @PreUpdate} lifecycle hooks defined in
 * {@link com.readyroad.readyroadbackend.domain.entity.BaseEntity}, so no
 * manual call is needed in service or controller layers.
 *
 * <h3>Characters replaced</h3>
 * <table>
 *   <tr><th>Unicode</th><th>Glyph</th><th>Name</th><th>Replaced by</th></tr>
 *   <tr><td>U+2019</td><td>'</td><td>RIGHT SINGLE QUOTATION MARK</td><td>U+0027 {@code '}</td></tr>
 *   <tr><td>U+2018</td><td>'</td><td>LEFT SINGLE QUOTATION MARK</td><td>U+0027 {@code '}</td></tr>
 *   <tr><td>U+201C</td><td>"</td><td>LEFT DOUBLE QUOTATION MARK</td><td>U+0022 {@code "}</td></tr>
 *   <tr><td>U+201D</td><td>"</td><td>RIGHT DOUBLE QUOTATION MARK</td><td>U+0022 {@code "}</td></tr>
 * </table>
 */
public final class TextNormalizer {

    private TextNormalizer() {
        // utility class — no instances
    }

    /**
     * Replaces typographic quote characters with their ASCII equivalents.
     *
     * @param text the input string (may be {@code null})
     * @return the sanitised string, or {@code null} if the input was {@code null}
     */
    public static String normalize(String text) {
        if (text == null) {
            return null;
        }
        return text
                .replace('\u2019', '\'')   // RIGHT SINGLE QUOTATION MARK  '  → '
                .replace('\u2018', '\'')   // LEFT SINGLE QUOTATION MARK   '  → '
                .replace('\u201C', '"')    // LEFT DOUBLE QUOTATION MARK   "  → "
                .replace('\u201D', '"');   // RIGHT DOUBLE QUOTATION MARK  "  → "
    }
}
