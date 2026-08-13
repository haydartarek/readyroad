package com.readyroad.readyroadbackend.marketing.content;

import com.readyroad.readyroadbackend.domain.entity.Lesson;
import com.readyroad.readyroadbackend.domain.entity.LessonPage;
import com.readyroad.readyroadbackend.domain.entity.QuizAnswerOption;
import com.readyroad.readyroadbackend.domain.entity.QuizQuestion;
import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.LessonRepository;
import com.readyroad.readyroadbackend.domain.repository.QuizQuestionRepository;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import com.readyroad.readyroadbackend.marketing.strategy.MarketingStrategyContextRequest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentSourceService {

    private final RoadSignRepository roadSignRepository;
    private final LessonRepository lessonRepository;
    private final QuizQuestionRepository questionRepository;
    private final JdbcTemplate jdbc;
    private final MarketingProperties properties;

    @Transactional(readOnly = true)
    public VerifiedContentSource load(ContentSourceType type, String id) {
        if (id == null || id.isBlank()) {
            throw new BlockedContentSourceException("Source id is required");
        }
        return switch (type) {
            case ROAD_SIGN -> roadSign(id.trim());
            case LESSON -> lesson(id.trim());
            case QUESTION -> question(id.trim());
            case YOUTUBE -> youtube(id.trim());
        };
    }

    private VerifiedContentSource roadSign(String signCode) {
        RoadSign sign = roadSignRepository.findFirstActiveBySignCodeCaseSensitive(signCode)
                .orElseThrow(() -> missing(ContentSourceType.ROAD_SIGN, signCode));
        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts = new EnumMap<>(ContentLocale.class);
        facts.put(ContentLocale.AR, localized(sign.getNameAr(), sign.getDescriptionAr(), sign.getSummaryAr(),
                sign.getDriverGuidanceAr(), join(sign.getExceptionsAr())));
        facts.put(ContentLocale.NL, localized(sign.getNameNl(), sign.getDescriptionNl(), sign.getSummaryNl(),
                sign.getDriverGuidanceNl(), join(sign.getExceptionsNl())));
        facts.put(ContentLocale.EN, localized(sign.getNameEn(), sign.getDescriptionEn(), sign.getSummaryEn(),
                sign.getDriverGuidanceEn(), join(sign.getExceptionsEn())));
        facts.put(ContentLocale.FR, localized(sign.getNameFr(), sign.getDescriptionFr(), sign.getSummaryFr(),
                sign.getDriverGuidanceFr(), join(sign.getExceptionsFr())));
        return verified(ContentSourceType.ROAD_SIGN, signCode, "ROAD_SIGN:" + signCode, facts, null);
    }

    private VerifiedContentSource lesson(String lessonCode) {
        Lesson source = lessonRepository.findByLessonCode(lessonCode)
                .filter(lesson -> Boolean.TRUE.equals(lesson.getIsActive()))
                .orElseThrow(() -> missing(ContentSourceType.LESSON, lessonCode));
        List<LessonPage> pages = source.getPages().stream()
                .sorted(Comparator.comparing(LessonPage::getPageNumber))
                .toList();
        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts = new EnumMap<>(ContentLocale.class);
        facts.put(ContentLocale.AR, lessonFacts(source.getTitleAr(), source.getDescriptionAr(), pages,
                LessonPage::getTitleAr, LessonPage::getContentAr, LessonPage::getBulletPointsAr));
        facts.put(ContentLocale.NL, lessonFacts(source.getTitleNl(), source.getDescriptionNl(), pages,
                LessonPage::getTitleNl, LessonPage::getContentNl, LessonPage::getBulletPointsNl));
        facts.put(ContentLocale.EN, lessonFacts(source.getTitleEn(), source.getDescriptionEn(), pages,
                LessonPage::getTitleEn, LessonPage::getContentEn, LessonPage::getBulletPointsEn));
        facts.put(ContentLocale.FR, lessonFacts(source.getTitleFr(), source.getDescriptionFr(), pages,
                LessonPage::getTitleFr, LessonPage::getContentFr, LessonPage::getBulletPointsFr));
        return verified(ContentSourceType.LESSON, lessonCode, "LESSON:" + lessonCode, facts, null);
    }

    private VerifiedContentSource question(String rawId) {
        long id;
        try {
            id = Long.parseLong(rawId);
        } catch (NumberFormatException error) {
            throw new BlockedContentSourceException("Question source id must be numeric");
        }
        QuizQuestion source = questionRepository.findByIdWithOptions(id)
                .filter(question -> Boolean.TRUE.equals(question.getIsActive())
                        && question.getStatus() == QuizQuestion.QuestionStatus.PUBLISHED)
                .orElseThrow(() -> missing(ContentSourceType.QUESTION, rawId));
        List<QuizAnswerOption> correct = source.getDeliverableOptions().stream()
                .filter(option -> Boolean.TRUE.equals(option.getIsCorrect()))
                .toList();
        if (correct.size() != 1) {
            throw new BlockedContentSourceException("Question source has no single verified correct answer");
        }
        QuizAnswerOption answer = correct.getFirst();
        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts = new EnumMap<>(ContentLocale.class);
        facts.put(ContentLocale.AR, questionFacts(source.getQuestionAr(), answer.getOptionTextAr(), source.getExplanationAr()));
        facts.put(ContentLocale.NL, questionFacts(source.getQuestionNl(), answer.getOptionTextNl(), source.getExplanationNl()));
        facts.put(ContentLocale.EN, questionFacts(source.getQuestionEn(), answer.getOptionTextEn(), source.getExplanationEn()));
        facts.put(ContentLocale.FR, questionFacts(source.getQuestionFr(), answer.getOptionTextFr(), source.getExplanationFr()));
        return verified(ContentSourceType.QUESTION, rawId, "QUESTION:" + rawId, facts, null);
    }

    private VerifiedContentSource youtube(String videoId) {
        try {
            return jdbc.queryForObject("""
                    SELECT y.title, y.description, c.usp_id, c.icp_id, c.content_pillar_id,
                           c.funnel_stage_id, c.conversion_goal_id
                    FROM youtube_videos y
                    JOIN content_items c ON c.source_type = 'YOUTUBE_VIDEO'
                        AND c.source_id = y.video_id
                        AND c.item_type = 'YOUTUBE_CONTENT_PACKAGE'
                    WHERE y.video_id = ?
                    ORDER BY c.id ASC
                    LIMIT 1
                    """, (rs, row) -> {
                        Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts = new EnumMap<>(ContentLocale.class);
                        for (ContentLocale locale : ContentLocale.SUPPORTED) {
                            facts.put(locale, localized(rs.getString("title"), rs.getString("description")));
                        }
                        var strategy = new MarketingStrategyContextRequest(
                                rs.getLong("usp_id"), rs.getString("icp_id"),
                                rs.getLong("content_pillar_id"), rs.getLong("funnel_stage_id"),
                                rs.getLong("conversion_goal_id"));
                        return verified(ContentSourceType.YOUTUBE, videoId, "YOUTUBE_VIDEO:" + videoId, facts, strategy);
                    }, videoId);
        } catch (EmptyResultDataAccessException error) {
            throw missing(ContentSourceType.YOUTUBE, videoId);
        }
    }

    private VerifiedContentSource verified(
            ContentSourceType type,
            String id,
            String reference,
            Map<ContentLocale, VerifiedContentSource.LocalizedFacts> facts,
            MarketingStrategyContextRequest embeddedStrategy) {
        StringBuilder canonical = new StringBuilder(type.name()).append('|').append(id);
        for (ContentLocale locale : ContentLocale.SUPPORTED) {
            var localized = facts.get(locale);
            if (localized == null || localized.title() == null || localized.title().isBlank()
                    || localized.facts() == null || localized.facts().isBlank()) {
                throw new BlockedContentSourceException("Source is missing verified " + locale + " content");
            }
            canonical.append('|').append(locale).append('|')
                    .append(ContentHashing.normalize(localized.title())).append('|')
                    .append(ContentHashing.normalize(localized.facts()));
        }
        return new VerifiedContentSource(type, id, reference, ContentHashing.sha256(canonical.toString()), facts,
                embeddedStrategy);
    }

    private VerifiedContentSource.LocalizedFacts lessonFacts(
            String title,
            String description,
            List<LessonPage> pages,
            java.util.function.Function<LessonPage, String> pageTitle,
            java.util.function.Function<LessonPage, String> pageContent,
            java.util.function.Function<LessonPage, String> pageBullets) {
        List<String> values = new ArrayList<>();
        values.add(description);
        for (LessonPage page : pages) {
            values.add(pageTitle.apply(page));
            values.add(pageContent.apply(page));
            values.add(pageBullets.apply(page));
        }
        return localized(title, values.toArray(String[]::new));
    }

    private VerifiedContentSource.LocalizedFacts questionFacts(String question, String answer, String explanation) {
        return localized(question, "Correct answer: " + safe(answer), "Explanation: " + safe(explanation));
    }

    private VerifiedContentSource.LocalizedFacts localized(String title, String... values) {
        String body = java.util.Arrays.stream(values)
                .filter(value -> value != null && !value.isBlank())
                .map(String::trim)
                .collect(java.util.stream.Collectors.joining("\n\n"));
        int limit = properties.getContent().getMaxSourceCharacters();
        if (body.length() > limit) {
            body = body.substring(0, limit).stripTrailing();
        }
        return new VerifiedContentSource.LocalizedFacts(safe(title), body);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    private static String join(List<String> values) {
        return values == null ? "" : values.stream()
                .filter(value -> value != null && !value.isBlank())
                .collect(java.util.stream.Collectors.joining("\n"));
    }

    private static BlockedContentSourceException missing(ContentSourceType type, String id) {
        return new BlockedContentSourceException(type + " source not found or not eligible: " + id);
    }
}
