package com.readyroad.readyroadbackend.marketing.editorial;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class EditorialArticleApprovalStore {

    private final JdbcTemplate jdbc;

    List<VersionSnapshot> currentVersions(long articleId) {
        return jdbc.query("""
                SELECT id, language, version_number
                FROM article_versions
                WHERE article_id = ? AND is_current
                ORDER BY language
                """, (result, rowNumber) -> new VersionSnapshot(
                        result.getLong("id"),
                        result.getString("language"),
                        result.getInt("version_number")), articleId);
    }

    record VersionSnapshot(long id, String language, int versionNumber) {
    }
}
