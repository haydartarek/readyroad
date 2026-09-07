package com.readyroad.readyroadbackend.exception;

import com.readyroad.readyroadbackend.marketing.editorial.EditorialWorkflowPrerequisiteException;
import com.readyroad.readyroadbackend.service.BackendMessageService;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Set;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.apache.catalina.connector.ClientAbortException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private BackendMessageService messages;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void authenticationExceptionReturnsUnifiedErrorAndMessagePayload() {
        when(messages.get("error.authentication.invalid_credentials")).thenReturn("Invalid credentials");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleAuthenticationException(new BadCredentialsException("bad credentials"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody())
                .containsEntry("error", "Invalid credentials")
                .containsEntry("message", "Invalid credentials")
                .containsKey("timestamp");
    }

    @Test
    void illegalArgumentReturnsUnifiedErrorAndMessagePayload() {
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleIllegalArgument(new IllegalArgumentException("Invalid category id"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("error", "Invalid category id")
                .containsEntry("message", "Invalid category id")
                .containsKey("timestamp");
    }

    @Test
    void constraintViolationReturnsUnifiedEnvelopeWithFields() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(messages.get("error.validation_failed")).thenReturn("Validation failed.");
        when(violation.getPropertyPath()).thenReturn(path);
        when(path.toString()).thenReturn("createSign.request.signCode");
        when(violation.getMessage()).thenReturn("must not be blank");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleConstraintViolation(new ConstraintViolationException(Set.of(violation)));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("error", "Validation failed.")
                .containsEntry("message", "Validation failed.")
                .containsKey("fields")
                .containsKey("timestamp");
        @SuppressWarnings("unchecked")
        Map<String, String> fields = (Map<String, String>) response.getBody().get("fields");
        assertThat(fields)
                .containsEntry("createSign.request.signCode", "must not be blank");
    }

    @Test
    void oversizedMultipartRequestReturnsBadRequestWithoutExposingInternals() {
        when(messages.get("upload.file_too_large_request", 5L))
                .thenReturn("The uploaded file exceeds the maximum allowed size of 5 MB.");

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleMaxUploadSizeExceeded(new MaxUploadSizeExceededException(5L * 1024L * 1024L));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody())
                .containsEntry("error", "The uploaded file exceeds the maximum allowed size of 5 MB.")
                .containsEntry("message", "The uploaded file exceeds the maximum allowed size of 5 MB.")
                .containsKey("timestamp");
    }

    @Test
    void editorialWorkflowPrerequisiteReturnsConflictWithStableCode() {
        String message = "Save a complete canonical draft before submitting it for review";

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler
                .handleEditorialWorkflowPrerequisite(new EditorialWorkflowPrerequisiteException(message));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody())
                .containsEntry("code", EditorialWorkflowPrerequisiteException.ERROR_CODE)
                .containsEntry("message", message)
                .containsKey("timestamp");
    }

    @Test
    void clientAbortDoesNotAttemptToCreateAnErrorResponse() {
        assertThat(globalExceptionHandler.handleGenericException(
                new ClientAbortException(new IOException("Broken pipe")))).isNull();
        verifyNoInteractions(messages);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Broken pipe", "Connection reset by peer"})
    void wrappedDisconnectedResponseDoesNotAttemptToCreateAnErrorResponse(String reason) {
        assertThat(globalExceptionHandler.handleGenericException(
                new HttpMessageNotWritableException("Image write failed", new IOException(reason))))
                .isNull();
        verifyNoInteractions(messages);
    }

    @Test
    void asyncDisconnectedResponseDoesNotAttemptToCreateAnErrorResponse() {
        assertThat(globalExceptionHandler.handleGenericException(
                new AsyncRequestNotUsableException("Response is no longer usable",
                        new ClientAbortException(new IOException("Broken pipe")))))
                .isNull();
        verifyNoInteractions(messages);
    }

    @ParameterizedTest
    @ValueSource(booleans = {false, true})
    void mvcHandlesAbortedImageWithoutWritingJsonOrLoggingSecondaryFailures(boolean committed) throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ImageResponseController())
                .setControllerAdvice(globalExceptionHandler).build();
        Logger rootLogger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        ListAppender<ILoggingEvent> appender = new ListAppender<>();
        appender.start();
        rootLogger.addAppender(appender);
        try {
            MockHttpServletResponse response = mvc.perform(get("/test/images/aborted")
                        .param("committed", Boolean.toString(committed)))
                .andReturn().getResponse();

            // DispatcherServlet clears Content-Type before invoking exception handlers.
            assertThat(response.getContentType()).isNull();
            assertThat(response.getContentLength()).isEqualTo(12);
            assertThat(response.getHeader("ETag")).isEqualTo("\"image-v1\"");
            assertThat(response.getContentAsByteArray()).isEmpty();
            assertThat(response.isCommitted()).isEqualTo(committed);
            // MockMvc has no disconnected socket: status 200 means no status was rewritten.
            assertThat(response.getStatus()).isEqualTo(200);
            assertThat(appender.list).noneMatch(event -> event.getLevel().isGreaterOrEqual(Level.WARN));
            verifyNoInteractions(messages);
        } finally {
            rootLogger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void realImageFailureStillReturnsSafeJson500InsteadOfAConverterFailure() throws Exception {
        when(messages.get("error.unexpected")).thenReturn("An unexpected error occurred.");
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ImageResponseController())
                .setControllerAdvice(globalExceptionHandler).build();

        mvc.perform(get("/test/images/failure"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("An unexpected error occurred.")))
                .andExpect(content().string(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("private-storage-path"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"unrelated-io", "socket-timeout", "conversion", "runtime"})
    void genuineFailuresStillReturn500(String failure) {
        when(messages.get("error.unexpected")).thenReturn("An unexpected error occurred.");
        Exception exception = switch (failure) {
            case "unrelated-io" -> new IOException("Cannot read image from storage");
            case "socket-timeout" -> new SocketTimeoutException("Read timed out");
            case "conversion" -> new HttpMessageNotWritableException("Unsupported output type");
            default -> new IllegalStateException("Unexpected application defect");
        };

        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).containsEntry("message", "An unexpected error occurred.");
        assertThat(response.getBody().toString()).doesNotContain(exception.getMessage());
    }

    @RestController
    static class ImageResponseController {
        @GetMapping("/test/images/aborted")
        void abortedImage(@org.springframework.web.bind.annotation.RequestParam boolean committed,
                HttpServletResponse response) throws IOException {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setContentLength(12);
            response.setHeader("ETag", "\"image-v1\"");
            if (committed) {
                response.flushBuffer();
            }
            throw new ClientAbortException(new IOException("Broken pipe"));
        }

        @GetMapping("/test/images/failure")
        void failedImage(HttpServletResponse response) throws IOException {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            throw new IOException("private-storage-path is unavailable");
        }
    }
}
