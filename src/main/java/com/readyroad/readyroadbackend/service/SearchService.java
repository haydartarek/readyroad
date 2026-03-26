package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.dto.response.SearchResponse;
import com.readyroad.readyroadbackend.dto.response.SearchResponse.SearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final RoadSignRepository roadSignRepository;
    private final LessonRepository lessonRepository;

    public SearchResponse search(String query, String language) {
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            return new SearchResponse(query, List.of());
        }

        List<SearchResultItem> results = new ArrayList<>();

        // Search road signs (limit 10)
        List<RoadSign> signs = roadSignRepository.searchRoadSigns(query.trim());
        signs.stream()
                .limit(10)
                .forEach(sign -> results.add(new SearchResultItem(
                        "traffic_sign",
                        sign.getSignCode(),
                        getTrafficSignName(sign, language),
                        getTrafficSignDescription(sign, language),
                        "/traffic-signs/" + sign.getSignCode())));

        // Search lessons (limit 10)
        List<Lesson> lessons = lessonRepository.searchLessons(query.trim());
        lessons.stream()
                .limit(10)
                .forEach(lesson -> results.add(new SearchResultItem(
                        "lesson",
                        lesson.getLessonCode(),
                        getLessonName(lesson, language),
                        getLessonDescription(lesson, language),
                        "/lessons/" + lesson.getLessonCode())));

        return new SearchResponse(query, results);
    }

    private String getTrafficSignName(RoadSign sign, String language) {
        return switch (language.toLowerCase()) {
            case "ar" -> sign.getNameAr();
            case "nl" -> sign.getNameNl();
            case "fr" -> sign.getNameFr();
            default -> sign.getNameEn();
        };
    }

    private String getTrafficSignDescription(RoadSign sign, String language) {
        String desc = switch (language.toLowerCase()) {
            case "ar" -> sign.getDescriptionAr();
            case "nl" -> sign.getDescriptionNl();
            case "fr" -> sign.getDescriptionFr();
            default -> sign.getDescriptionEn();
        };
        return desc != null && desc.length() > 100 ? desc.substring(0, 100) + "..." : (desc != null ? desc : "");
    }

    private String getLessonName(Lesson lesson, String language) {
        return switch (language.toLowerCase()) {
            case "ar" -> lesson.getTitleAr();
            case "nl" -> lesson.getTitleNl();
            case "fr" -> lesson.getTitleFr();
            default -> lesson.getTitleEn();
        };
    }

    private String getLessonDescription(Lesson lesson, String language) {
        String desc = switch (language.toLowerCase()) {
            case "ar" -> lesson.getDescriptionAr();
            case "nl" -> lesson.getDescriptionNl();
            case "fr" -> lesson.getDescriptionFr();
            default -> lesson.getDescriptionEn();
        };
        return desc != null && desc.length() > 100 ? desc.substring(0, 100) + "..." : (desc != null ? desc : "");
    }
}
