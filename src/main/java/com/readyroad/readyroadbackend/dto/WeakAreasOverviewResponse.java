package com.readyroad.readyroadbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Story C2: Weak Areas Overview Response
 * Wraps the list of weak-area recommendations with summary statistics
 * so the frontend can display accurate overall accuracy and category counts.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeakAreasOverviewResponse {

    /**
     * All categories where accuracy is below the 80% target and the user
     * has attempted at least 5 questions. Sorted weakest-first.
     */
    private List<WeakAreaRecommendationResponse> weakAreas;

    /**
     * Total number of categories where the user has attempted >= 5 questions.
     * Used by the frontend to derive "Strong Areas" = totalPracticedCategories - weakAreas.size()
     */
    private int totalPracticedCategories;

    /**
     * Real overall accuracy across ALL practiced categories (weighted average:
     * total correct / total attempted * 100).
     * Provided here so the frontend does not have to approximate it from the
     * weak-areas subset.
     */
    private double overallAccuracy;
}
