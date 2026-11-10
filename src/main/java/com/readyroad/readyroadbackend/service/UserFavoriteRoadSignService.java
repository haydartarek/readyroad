package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/** JWT-scoped canonical sign favorites. Idempotent add/remove, no client-supplied owner. */
@Service
@RequiredArgsConstructor
public class UserFavoriteRoadSignService {
    private final JdbcTemplate jdbc;
    private final RoadSignRepository roadSigns;
    private final CanonicalSignCatalogService canonicalCatalog;

    @Transactional(readOnly = true)
    public List<Long> list(Long userId) {
        List<Long> candidateIds = jdbc.queryForList("""
                SELECT road_sign_id FROM user_favorite_road_signs
                WHERE user_id = ? ORDER BY created_at ASC, road_sign_id ASC
                """, Long.class, userId);
        if (candidateIds.isEmpty()) {
            return List.of();
        }
        Map<Long, RoadSign> signsById = roadSigns.findAllById(candidateIds).stream()
                .collect(Collectors.toMap(RoadSign::getId, Function.identity()));
        List<Long> publicIds = new ArrayList<>();
        for (Long signId : candidateIds) {
            RoadSign sign = signsById.get(signId);
            if (sign != null && Boolean.TRUE.equals(sign.getIsActive())
                    && canonicalCatalog.isPubliclyAllowed(sign)) {
                publicIds.add(signId);
            }
        }
        return publicIds;
    }

    @Transactional
    public void add(Long userId, Long roadSignId) {
        RoadSign sign = roadSigns.findById(roadSignId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sign not found"));
        if (!Boolean.TRUE.equals(sign.getIsActive()) || !canonicalCatalog.isPubliclyAllowed(sign)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sign not available");
        }
        jdbc.update("""
                INSERT INTO user_favorite_road_signs(user_id, road_sign_id)
                VALUES (?, ?) ON CONFLICT (user_id, road_sign_id) DO NOTHING
                """, userId, roadSignId);
    }

    @Transactional
    public void remove(Long userId, Long roadSignId) {
        jdbc.update("DELETE FROM user_favorite_road_signs WHERE user_id = ? AND road_sign_id = ?",
                userId, roadSignId);
    }
}
