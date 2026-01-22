package com.readyroad.readyroadbackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global Exception Handler for ReadyRoad Backend
 *
 * Provides consistent error responses across all controllers
 */
@ControllerAdvice
public class GlobalExceptionHandler {

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
}
