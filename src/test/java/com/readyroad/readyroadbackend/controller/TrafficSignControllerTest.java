package com.readyroad.readyroadbackend.controller;

import com.readyroad.readyroadbackend.dto.response.TrafficSignResponse;
import com.readyroad.readyroadbackend.service.TrafficSignService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrafficSignControllerTest {

    @Mock
    private TrafficSignService trafficSignService;

    @InjectMocks
    private TrafficSignController trafficSignController;

    @Test
    void collectionRouteAcceptsOptionalCategoryAndQueryFilters() {
        List<TrafficSignResponse> expected = List.of();
        when(trafficSignService.getFilteredPublicSigns(7L, "bend")).thenReturn(expected);

        ResponseEntity<List<TrafficSignResponse>> response = trafficSignController.getAllSigns(7L, "bend");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
        verify(trafficSignService).getFilteredPublicSigns(7L, "bend");
    }

    @Test
    void legacySearchRouteDelegatesToFilteredCollectionService() {
        List<TrafficSignResponse> expected = List.of();
        when(trafficSignService.getFilteredPublicSigns(null, "priority")).thenReturn(expected);

        ResponseEntity<List<TrafficSignResponse>> response = trafficSignController.searchTrafficSigns("priority");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
        verify(trafficSignService).getFilteredPublicSigns(null, "priority");
    }

    @Test
    void legacyCategoryRouteDelegatesToFilteredCollectionService() {
        List<TrafficSignResponse> expected = List.of();
        when(trafficSignService.getFilteredPublicSigns(3L, null)).thenReturn(expected);

        ResponseEntity<List<TrafficSignResponse>> response = trafficSignController.getSignsByCategory(3L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);
        verify(trafficSignService).getFilteredPublicSigns(3L, null);
    }
}