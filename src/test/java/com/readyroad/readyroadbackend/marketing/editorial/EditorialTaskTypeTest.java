package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

class EditorialTaskTypeTest {

    @Test
    void mapsTheExactSeventeenMasterV3EditorialTaskTypes() {
        assertThat(Arrays.stream(EditorialTaskType.values()).map(Enum::name)).containsExactly(
                "ARTICLE_OPPORTUNITY_DISCOVERY",
                "ARTICLE_KEYWORD_CLUSTERING",
                "ARTICLE_BRIEF_CREATE",
                "ARTICLE_SOURCE_COLLECT",
                "ARTICLE_DRAFT_CREATE",
                "ARTICLE_FACT_CHECK",
                "ARTICLE_LEGAL_REVIEW",
                "ARTICLE_TRANSLATION_ADAPT",
                "ARTICLE_DUPLICATE_CHECK",
                "ARTICLE_CANNIBALIZATION_CHECK",
                "ARTICLE_INTERNAL_LINK_PLAN",
                "ARTICLE_WAITING_APPROVAL",
                "ARTICLE_PUBLISH",
                "ARTICLE_UPDATE",
                "ARTICLE_PERFORMANCE_SNAPSHOT",
                "ARTICLE_REFRESH_RECOMMENDATION",
                "ARTICLE_ARCHIVE_RECOMMENDATION");
        assertThat(EditorialTaskType.ARTICLE_WAITING_APPROVAL.runtimeTaskType())
                .isEqualTo("ARTICLE_APPROVAL");
        assertThat(EditorialTaskType.fromRuntimeTaskType("ARTICLE_APPROVAL"))
                .contains(EditorialTaskType.ARTICLE_WAITING_APPROVAL);
    }
}
