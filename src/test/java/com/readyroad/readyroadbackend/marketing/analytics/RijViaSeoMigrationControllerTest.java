package com.readyroad.readyroadbackend.marketing.analytics;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.readyroad.readyroadbackend.exception.GlobalExceptionHandler;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class RijViaSeoMigrationControllerTest {
    @Test
    void invalidWorkbookReturnsActionableBadRequestInsteadOfGenericServerError() throws Exception {
        var service = mock(RijViaSeoMigrationService.class);
        when(service.importWorkbook(any(), eq("admin")))
                .thenThrow(new InvalidSearchConsoleWorkbookException("Required Search Console sheets are missing"));
        var mvc = MockMvcBuilders.standaloneSetup(new RijViaSeoMigrationController(service))
                .setControllerAdvice(new GlobalExceptionHandler(mock(BackendMessageService.class)))
                .build();
        mvc.perform(multipart("/api/admin/marketing/seo-migration/import")
                        .file(new MockMultipartFile("file", "coverage.xlsx", "application/octet-stream", new byte[]{1}))
                        .principal(() -> "admin"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Required Search Console sheets are missing"));
    }
}
