package com.readyroad.readyroadbackend.dto.response;

import java.util.List;

public record TrafficSignResponse(
                Long id,
                String signCode,
                String categoryCode,
                String routeCode,
                Integer exam1TotalQuestions,
                Integer exam1PassingScore,
                String nameAr,
                String nameEn,
                String nameNl,
                String nameFr,
                String summaryAr,
                String summaryEn,
                String summaryNl,
                String summaryFr,
                String descriptionAr,
                String descriptionEn,
                String descriptionNl,
                String descriptionFr,
                String driverGuidanceAr,
                String driverGuidanceEn,
                String driverGuidanceNl,
                String driverGuidanceFr,
                List<String> exceptionsAr,
                List<String> exceptionsEn,
                List<String> exceptionsNl,
                List<String> exceptionsFr,
                String imageUrl) {
}
