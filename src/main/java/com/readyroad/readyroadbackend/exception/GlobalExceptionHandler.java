package com.readyroad.readyroadbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import jakarta.validation.ConstraintViolationException;

import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler for ReadyRoad Backend
 *
 * Provides consistent error responses across all controllers
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle all Spring Security AuthenticationExceptions:
     * BadCredentialsException (wrong password), UsernameNotFoundException (no
     * account),
     * DisabledException (inactive account), LockedException (locked account).
     *
     * Always returns HTTP 401 with a generic message to prevent user enumeration.
     * HTTP 401 UNAUTHORIZED
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication failed: {}", ex.getClass().getSimpleName());
        Map<String, Object> error = new HashMap<>();
        error.put("message", "Invalid username or password.");
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Handle ActiveExamAlreadyExistsException - user already has active exam
     * HTTP 409 CONFLICT
     */
    @ExceptionHandler(ActiveExamAlreadyExistsException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleActiveExamExists(ActiveExamAlreadyExistsException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "ActiveExamAlreadyExistsException");
        error.put("message", ex.getMessage());
        error.put("activeExamId", ex.getActiveExamId());
        error.put("userId", ex.getUserId());
        error.put("code", "ACTIVE_EXAM_EXISTS");
        error.put("resolution",
                "Cancel the active exam using DELETE /api/exams/simulations/active before starting a new one");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle ExamExpiredException - exam time limit exceeded
     * HTTP 409 CONFLICT
     */
    @ExceptionHandler(ExamExpiredException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleExamExpired(ExamExpiredException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "ExamExpiredException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Exam has expired");
        error.put("examId", ex.getExamId());
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle ExamNotActiveException - exam is not in active state
     * HTTP 409 CONFLICT
     */
    @ExceptionHandler(ExamNotActiveException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleExamNotActive(ExamNotActiveException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "ExamNotActiveException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Exam is not active");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handle ExamNotCompletedException - trying to view results before completion
     * HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(ExamNotCompletedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleExamNotCompleted(ExamNotCompletedException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "ExamNotCompletedException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Exam is not completed yet");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle ExamNotFoundException - exam ID not found
     * HTTP 404 NOT FOUND
     */
    @ExceptionHandler(ExamNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleExamNotFound(ExamNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "ExamNotFoundException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Exam not found");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle QuestionNotFoundException - question not found in exam
     * HTTP 404 NOT FOUND
     */
    @ExceptionHandler(QuestionNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleQuestionNotFound(QuestionNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "QuestionNotFoundException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Question not found");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle InvalidAnswerException - invalid answer submission
     * HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(InvalidAnswerException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleInvalidAnswer(InvalidAnswerException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "InvalidAnswerException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Invalid answer");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handle UnauthorizedException - access denied
     * HTTP 403 FORBIDDEN
     */
    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleUnauthorized(UnauthorizedException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "UnauthorizedException");
        error.put("message", ex.getMessage() != null && !ex.getMessage().isEmpty()
                ? ex.getMessage()
                : "Access denied");
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    /**
     * Handle MethodArgumentNotValidException - @Valid @RequestBody validation
     * failures
     * HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        Map<String, Object> body = new HashMap<>();
        body.put("error", "Validation failed");
        body.put("fields", fieldErrors);
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Handle ConstraintViolationException - @Min/@Max/@NotNull violations on
     * request params
     * HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            errors.put(field, violation.getMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handle TrafficSignNotFoundException - sign code not found
     * HTTP 404 NOT FOUND
     */
    @ExceptionHandler(TrafficSignNotFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleTrafficSignNotFound(TrafficSignNotFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "TrafficSignNotFoundException");
        error.put("message", ex.getMessage());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handle IllegalArgumentException - invalid method arguments
     * HTTP 400 BAD REQUEST
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handle NoResourceFoundException — static resource not found (image/file).
     * Prevents the generic Exception handler from returning 500 for missing files.
     * HTTP 404 NOT FOUND
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleNoResourceFound(NoResourceFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Resource not found");
        error.put("path", ex.getResourcePath());
        error.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Generic fallback — catch all unhandled exceptions
     * Logs the real error internally but returns a safe 500 response.
     * HTTP 500 INTERNAL SERVER ERROR
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: {}", ex.getMessage(), ex);
        Map<String, Object> body = new HashMap<>();
        body.put("error", "An unexpected error occurred");
        body.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}