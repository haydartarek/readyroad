package com.readyroad.readyroadbackend.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

class TheoryTaxonomyMigrationTest {

    private static final Pattern MAPPING = Pattern.compile(
            "WHERE q\\.id IN \\(([^)]+)\\);",
            Pattern.MULTILINE);

    @Test
    void mapsEveryCurrentQuestionExactlyOnceToEightTheoryCategories() throws IOException {
        String sql;
        try (var stream = getClass().getResourceAsStream(
                "/db/migration-postgresql/V21__Consolidate_Theory_Taxonomy.sql")) {
            assertThat(stream).isNotNull();
            sql = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }

        Set<Long> questionIds = new HashSet<>();
        Matcher matcher = MAPPING.matcher(sql);
        int mappingGroups = 0;
        while (matcher.find()) {
            mappingGroups++;
            Arrays.stream(matcher.group(1).split(","))
                    .map(String::trim)
                    .map(Long::valueOf)
                    .forEach(id -> assertThat(questionIds.add(id))
                            .as("question %s must have one primary category", id)
                            .isTrue());
        }

        assertThat(mappingGroups).isEqualTo(8);
        assertThat(questionIds).hasSize(61);
        assertThat(sql).contains("'TH01'", "'TH02'", "'TH03'", "'TH04'",
                "'TH05'", "'TH06'", "'TH07'", "'TH08'");
        assertThat(sql).contains("IF question_count <> mapped_count THEN");
        assertThat(sql).doesNotContain("question_count <> 61");
        assertThat(sql.toUpperCase()).doesNotContain("DELETE FROM QUIZ_QUESTIONS");
        assertThat(sql.toUpperCase()).doesNotContain("DELETE FROM QUIZ_ANSWER_OPTIONS");
        assertThat(sql.toUpperCase()).doesNotContain("DELETE FROM EXAM_SIMULATIONS");
    }
}
