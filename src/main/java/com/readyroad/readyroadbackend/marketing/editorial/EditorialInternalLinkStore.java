package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialInternalLinkStore {

    private final JdbcTemplate jdbc;

    Optional<Long> publishedArticleId(String language, String slug) {
        return jdbc.queryForList("""
                SELECT DISTINCT publication.article_id
                FROM article_publications publication
                JOIN article_versions version ON version.id = publication.article_version_id
                JOIN articles article ON article.id = publication.article_id
                WHERE publication.language = ?
                  AND lower(publication.published_slug) = lower(?)
                  AND publication.status = 'PUBLISHED'
                  AND version.status = 'PUBLISHED'
                  AND article.lifecycle_state <> 'ARCHIVED'
                ORDER BY publication.article_id
                LIMIT 1
                """, Long.class, language, slug).stream().findFirst();
    }

    boolean lessonExists(String lessonCode, Integer pageNumber) {
        Boolean exists = pageNumber == null
                ? jdbc.queryForObject("""
                        SELECT EXISTS (
                            SELECT 1 FROM lessons
                            WHERE lower(lesson_code) = lower(?) AND is_active
                        )
                        """, Boolean.class, lessonCode)
                : jdbc.queryForObject("""
                        SELECT EXISTS (
                            SELECT 1
                            FROM lessons lesson
                            JOIN lesson_pages page ON page.lesson_id = lesson.id
                            WHERE lower(lesson.lesson_code) = lower(?)
                              AND lesson.is_active
                              AND page.page_number = ?
                        )
                        """, Boolean.class, lessonCode, pageNumber);
        return Boolean.TRUE.equals(exists);
    }

    boolean trafficSignExists(String signCode) {
        Boolean exists = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM road_signs
                    WHERE lower(sign_code) = lower(?) AND is_active
                )
                """, Boolean.class, signCode);
        return Boolean.TRUE.equals(exists);
    }

    boolean practiceCategoryExists(String categoryCode) {
        Boolean exists = jdbc.queryForObject("""
                SELECT EXISTS (
                    SELECT 1 FROM categories
                    WHERE lower(code) = lower(?) AND is_active
                )
                """, Boolean.class, categoryCode);
        return Boolean.TRUE.equals(exists);
    }
}
