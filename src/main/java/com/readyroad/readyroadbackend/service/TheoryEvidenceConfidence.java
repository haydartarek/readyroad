package com.readyroad.readyroadbackend.service;

final class TheoryEvidenceConfidence {

    private TheoryEvidenceConfidence() {
    }

    static String state(long uniqueAnswered, long eligibleInventory) {
        if (eligibleInventory <= 0 || uniqueAnswered < 5) {
            return "LOW";
        }
        long highEvidence = Math.min(20, eligibleInventory);
        long mediumEvidence = Math.min(10, eligibleInventory);
        double inventoryCoverage = uniqueAnswered / (double) eligibleInventory;
        if (uniqueAnswered >= highEvidence && inventoryCoverage >= 0.50) {
            return "HIGH";
        }
        if (uniqueAnswered >= mediumEvidence && inventoryCoverage >= 0.25) {
            return "MEDIUM";
        }
        return "LOW";
    }

    static int score(long uniqueAnswered, long eligibleInventory) {
        if (eligibleInventory <= 0 || uniqueAnswered <= 0) {
            return 0;
        }

        long highEvidenceTarget = Math.max(
                5,
                Math.max(
                        Math.min(20, eligibleInventory),
                        (long) Math.ceil(eligibleInventory * 0.50)));
        int rawScore = (int) Math.min(100, Math.round(uniqueAnswered * 100.0 / highEvidenceTarget));
        return switch (state(uniqueAnswered, eligibleInventory)) {
            case "HIGH" -> Math.max(80, rawScore);
            case "MEDIUM" -> Math.max(40, Math.min(79, rawScore));
            default -> Math.min(39, rawScore);
        };
    }

    static boolean supportsMasteryClaim(long uniqueAnswered, long eligibleInventory) {
        return !"LOW".equals(state(uniqueAnswered, eligibleInventory));
    }
}
