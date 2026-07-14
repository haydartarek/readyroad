package com.readyroad.readyroadbackend.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.readyroad.readyroadbackend.domain.entity.ImportHistory;
import com.readyroad.readyroadbackend.domain.repository.ImportHistoryRepository;
import com.readyroad.readyroadbackend.dto.response.ImportHistoryResponse;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import com.readyroad.readyroadbackend.service.DataImportService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class DataImportControllerTest {

    @Mock
    private DataImportService dataImportService;

    @Mock
    private ImportHistoryRepository importHistoryRepository;

    @Mock
    private BackendMessageService messages;

    @InjectMocks
    private DataImportController dataImportController;

    @Test
    void getHistoryMapsEntityToResponseDtoWithoutChecksum() {
        ImportHistory history = new ImportHistory();
        history.setId(3L);
        history.setPerformedBy("admin");
        history.setPerformedAt(LocalDateTime.of(2026, 5, 11, 20, 30));
        history.setImportType("categories");
        history.setFileName("category_descriptions.json");
        history.setFileChecksum("secret-checksum");
        history.setDryRun(true);
        history.setCreatedCount(1);
        history.setUpdatedCount(2);
        history.setSkippedCount(3);
        history.setStatus("SUCCESS");
        history.setErrorSummary(null);
        history.setWarningSummary("minor warning");
        when(importHistoryRepository.findTop20ByOrderByPerformedAtDesc()).thenReturn(List.of(history));

        var response = dataImportController.getHistory();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsExactly(new ImportHistoryResponse(
                3L,
                "admin",
                LocalDateTime.of(2026, 5, 11, 20, 30),
                "categories",
                "category_descriptions.json",
                true,
                1,
                2,
                3,
                "SUCCESS",
                null,
                "minor warning"));
    }

    @Test
    void getHistoryDetailReturnsUnifiedNotFoundPayload() {
        when(messages.get("error.resource_not_found")).thenReturn("Resource not found.");
        when(importHistoryRepository.findById(99L)).thenReturn(Optional.empty());

        var response = dataImportController.getHistoryDetail(99L);
        @SuppressWarnings("unchecked")
        var body = (java.util.Map<String, Object>) response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(body)
                .containsEntry("error", "Resource not found.")
                .containsEntry("message", "Resource not found.")
                .containsKey("timestamp");
    }

    @Test
    void legacySignsUploadTypeIsRejected() {
        MockMultipartFile file = new MockMultipartFile(
                "file", "legacy.json", "application/json", "[]".getBytes());
        var response = dataImportController.preview("signs", file, null);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        verifyNoInteractions(dataImportService);
    }
}
