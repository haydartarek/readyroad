package com.readyroad.readyroadbackend.marketing.editorial;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
class EditorialPriorityScorer {

    EditorialPriorityScore score(
            EditorialPriorityConfig config,
            Map<String, EditorialFactorEvidence> evidence) {
        Map<String, BigDecimal> normalized = new LinkedHashMap<>();
        Map<String, String> states = new LinkedHashMap<>();
        BigDecimal finalScore = BigDecimal.ZERO;
        for (var weight : config.weights().entrySet()) {
            EditorialFactorEvidence factor = evidence.getOrDefault(
                    weight.getKey(), EditorialFactorEvidence.missing());
            BigDecimal value = factor.normalizedScore();
            if (EditorialPriorityConfig.SEARCH_CONSOLE.equals(weight.getKey())
                    && factor.state() == EvidenceState.MISSING) {
                value = config.missingSearchConsolePercent();
            }
            value = clamp(value);
            normalized.put(weight.getKey(), value);
            states.put(weight.getKey(), factor.state().name());
            finalScore = finalScore.add(weight.getValue()
                    .multiply(value)
                    .divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        }
        finalScore = finalScore.setScale(3, RoundingMode.HALF_UP);
        return new EditorialPriorityScore(
                finalScore,
                priority(finalScore, config),
                Map.copyOf(normalized),
                Map.copyOf(states));
    }

    private static String priority(BigDecimal score, EditorialPriorityConfig config) {
        if (score.compareTo(config.p0()) >= 0) return "P0";
        if (score.compareTo(config.p1()) >= 0) return "P1";
        if (score.compareTo(config.p2()) >= 0) return "P2";
        return "P3";
    }

    private static BigDecimal clamp(BigDecimal value) {
        if (value == null || value.signum() < 0) return BigDecimal.ZERO;
        return value.min(BigDecimal.valueOf(100));
    }

    enum EvidenceState { PRESENT, MISSING }

    record EditorialFactorEvidence(BigDecimal normalizedScore, EvidenceState state) {
        static EditorialFactorEvidence present(double score) {
            return new EditorialFactorEvidence(BigDecimal.valueOf(score), EvidenceState.PRESENT);
        }

        static EditorialFactorEvidence missing() {
            return new EditorialFactorEvidence(BigDecimal.ZERO, EvidenceState.MISSING);
        }
    }

    record EditorialPriorityScore(
            BigDecimal finalScore,
            String priority,
            Map<String, BigDecimal> normalizedScores,
            Map<String, String> evidenceStates) {}
}
