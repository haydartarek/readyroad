package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.TrafficSign;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.TrafficSignRepository;
import com.readyroad.readyroadbackend.dto.response.SearchResponse;
import com.readyroad.readyroadbackend.dto.response.SearchResponse.SearchResultItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final TrafficSignRepository trafficSignRepository;
    private final LessonRepository lessonRepository;

    public SearchResponse search(String query, String language) {
        if (query == null || query.trim().isEmpty() || query.length() < 2) {
            return new SearchResponse(query, List.of());
        }

        String searchTerm = "%" + query.toLowerCase() + "%";
        List<SearchResultItem> results = new ArrayList<>();

        // Search traffic signs (limit 10)
        List<TrafficSign> signs = trafficSignRepository.searchTrafficSigns(searchTerm);
        signs.stream()
                .limit(10)
                .forEach(sign -> results.add(new SearchResultItem(
                        "traffic_sign",
                        sign.getSignCode(),
                        getTrafficSignName(sign, language),
                        getTrafficSignDescription(sign, language),
                        "/traffic-signs/" + sign.getSignCode()
                )));

        // Search lessons (limit 10)
        List<Lesson> lessons = lessonRepository.searchLessons(searchTerm);
        lessons.stream()
                .limit(10)
                .forEach(lesson -> results.add(new SearchResultItem(
                        "lesson",
                        String.valueOf(lesson.getId()),
                        getLessonTitle(lesson, language),
                        "", // No description for lessons
                        "/lessons/" + lesson.getId()
                )));

        return new SearchResponse(query, results);
    }

    private String getTrafficSignName(TrafficSign sign, String language) {
        return switch (language.toLowerCase()) {
            case "ar" -> sign.getNameAr();
            case "nl" -> sign.getNameNl();
            case "fr" -> sign.getNameFr();
            default -> sign.getNameEn();
        };
    }

    private String getTrafficSignDescription(TrafficSign sign, String language) {
        String desc = switch (language.toLowerCase()) {
            case "ar" -> sign.getDescriptionAr();
            case "nl" -> sign.getDescriptionNl();
            case "fr" -> sign.getDescriptionFr();
            default -> sign.getDescriptionEn();
        };
        return desc != null && desc.length() > 100 ? desc.substring(0, 100) + "..." : (desc != null ? desc : "");
    }

    private String getLessonTitle(Lesson lesson, String language) {
        return switch (language.toLowerCase()) {
            case "ar" -> lesson.getTitleAr();
            case "nl" -> lesson.getTitleNl();
            case "fr" -> lesson.getTitleFr();
            default -> lesson.getTitleEn();
        };
    }
}
