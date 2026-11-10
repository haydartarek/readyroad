package com.readyroad.readyroadbackend.service;

import com.readyroad.readyroadbackend.domain.entity.RoadSign;
import com.readyroad.readyroadbackend.domain.repository.RoadSignRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserFavoriteRoadSignServiceTest {
    JdbcTemplate jdbc;
    RoadSignRepository signs;
    CanonicalSignCatalogService canonical;
    UserFavoriteRoadSignService service;

    @BeforeEach void init() {
        jdbc = mock(JdbcTemplate.class);
        signs = mock(RoadSignRepository.class);
        canonical = mock(CanonicalSignCatalogService.class);
        service = new UserFavoriteRoadSignService(jdbc, signs, canonical);
    }

    @Test void emptyFavoritesAreEmpty() {
        when(jdbc.queryForList(anyString(), eq(Long.class), eq(9L))).thenReturn(List.of());
        assertThat(service.list(9L)).isEmpty();
        verify(signs, never()).findAllById(any());
    }

    @Test void listIsScopedToJwtOwnerAndFiltersUnavailableSigns() {
        when(jdbc.queryForList(anyString(), eq(Long.class), eq(9L)))
                .thenReturn(List.of(3L, 4L));
        RoadSign publicSign = new RoadSign();
        publicSign.setId(3L); publicSign.setIsActive(true);
        RoadSign inactive = new RoadSign();
        inactive.setId(4L); inactive.setIsActive(false);
        when(signs.findAllById(List.of(3L, 4L))).thenReturn(List.of(publicSign, inactive));
        when(canonical.isPubliclyAllowed(publicSign)).thenReturn(true);
        assertThat(service.list(9L)).containsExactly(3L);
        verify(jdbc).queryForList(anyString(), eq(Long.class), eq(9L));
    }

    @Test void addIsIdempotentAndScopedToOwner() {
        RoadSign sign = new RoadSign();
        sign.setId(25L); sign.setIsActive(true);
        when(signs.findById(25L)).thenReturn(Optional.of(sign));
        when(canonical.isPubliclyAllowed(sign)).thenReturn(true);
        service.add(9L,25L);
        verify(jdbc).update(contains("ON CONFLICT"), eq(9L), eq(25L));
    }

    @Test void inactiveSignsCannotBeSaved() {
        RoadSign sign = new RoadSign();
        sign.setIsActive(false);
        when(signs.findById(25L)).thenReturn(Optional.of(sign));
        assertThatThrownBy(() -> service.add(9L, 25L))
            .isInstanceOf(ResponseStatusException.class)
            .satisfies(e -> assertThat(((ResponseStatusException)e).getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND));
        verify(jdbc, never()).update(anyString(), any(), any());
    }

    @Test void deletionIsIdempotentAndScopedToOwner() {
        service.remove(9L, 25L);
        verify(jdbc).update(contains("WHERE user_id = ?"), eq(9L), eq(25L));
    }
}
