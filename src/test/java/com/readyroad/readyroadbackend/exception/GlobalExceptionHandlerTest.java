package com.readyroad.readyroadbackend.exception;

import com.readyroad.readyroadbackend.service.BackendMessageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.Set;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}