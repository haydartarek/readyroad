package com.readyroad.readyroadbackend.marketing.editorial;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

class EditorialPriorityScorerTest {

    private final EditorialPriorityScorer scorer = new EditorialPriorityScorer();
    private final EditorialPriorityConfig config = EditorialPriorityConfig.defaults();

    @Test
    void classifiesEveryApprovedBoundary() {
        assertThat(scoreFor(80).priority()).isEqualTo("P0");
        assertThat(scoreFor(79.999).priority()).isEqualTo("P1");
        assertThat(scoreFor(60).priority()).isEqualTo("P1");
        assertThat(scoreFor(59.999).priority()).isEqualTo("P2");
        assertThat(scoreFor(40).priority()).isEqualTo("P2");
        assertThat(scoreFor(39.999).priority()).isEqualTo("P3");
        assertThat(scoreFor(0).priority()).isEqualTo("P3");
    }

    @Test
    void missingSearchConsoleUsesNeutralHalfWeightWithoutInventingMetrics() {
        var score = scorer.score(config, java.util.Map.of());

        assertThat(score.finalScore()).isEqualByComparingTo("10.000");
        assertThat(score.normalizedScores().get(EditorialPriorityConfig.SEARCH_CONSOLE))
                .isEqualByComparingTo("50");
        assertThat(score.evidenceStates().get(EditorialPriorityConfig.SEARCH_CONSOLE))
                .isEqualTo("MISSING");
        assertThat(score.priority()).isEqualTo("P3");
    }

    private EditorialPriorityScorer.EditorialPriorityScore scoreFor(double normalizedScore) {
        var factors = new HashMap<String, EditorialPriorityScorer.EditorialFactorEvidence>();
        config.weights().keySet().forEach(key -> factors.put(
                key, EditorialPriorityScorer.EditorialFactorEvidence.present(normalizedScore)));
        return scorer.score(config, factors);
    }
}
