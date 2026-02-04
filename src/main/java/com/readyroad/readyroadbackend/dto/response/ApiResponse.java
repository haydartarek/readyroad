package com.readyroad.readyroadbackend.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Generic API Response wrapper for consistent response structure
 *
 * This class provides a standardized format for all API responses,
 * making it easier for frontend applications to handle responses uniformly.
 *
 * Example usage:
 * <pre>
 * // Success response
 * ApiResponse.success("Operation completed", data);
 *
 * // Error response
 * ApiResponse.error("Operation failed", errorDetails);
 * </pre>
 *
 * @param <T> The type of data being returned in the response
 * @author ReadyRoad Team
 * @version 1.0
 * @since 2026-02-04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /**
     * Indicates whether the request was successful
     * true = success, false = error
     */
    private boolean success;

    /**
     * HTTP status code
     * Examples: 200 (OK), 201 (Created), 400 (Bad Request), 404 (Not Found)
     */
    private Integer status;

    /**
     * Human-readable message describing the result
     * Should be clear and informative for both developers and end-users
     */
    private String message;

    /**
     * The actual data payload
     * Can be any type: object, list, primitive, etc.
     */
    private T data;

    /**
     * Timestamp of when the response was generated (milliseconds since epoch)
     */
    private Long timestamp;

    /**
     * Error details (only populated on failure)
     * Contains technical error information for debugging
     */
    private String error;

    /**
     * Path of the request that generated this response
     */
    private String path;

    // ========================================
    // Factory Methods for Success Responses
    // ========================================

    /**
     * Creates a successful response with data (200 OK)
     *
     * @param data the response data
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message("Operation successful")
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a successful response with custom message and data
     *
     * @param message custom success message
     * @param data the response data
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a successful response with custom status code
     *
     * @param status HTTP status code (e.g., 201 for Created)
     * @param message custom success message
     * @param data the response data
     * @param <T> the type of data
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> success(Integer status, String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a successful response without data
     * Useful for operations that don't return data (e.g., DELETE)
     *
     * @param message success message
     * @param <T> the type parameter
     * @return ApiResponse with success status
     */
    public static <T> ApiResponse<T> successNoData(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(200)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a successful created response (201)
     *
     * @param message success message
     * @param data the created resource
     * @param <T> the type of data
     * @return ApiResponse with 201 status
     */
    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .status(201)
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ========================================
    // Factory Methods for Error Responses
    // ========================================

    /**
     * Creates an error response (400 Bad Request)
     *
     * @param message error message
     * @param <T> the type parameter
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(400)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates an error response with details
     *
     * @param message user-friendly error message
     * @param error technical error details
     * @param <T> the type parameter
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(400)
                .message(message)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates an error response with custom status code
     *
     * @param status HTTP error status code (e.g., 404, 500)
     * @param message error message
     * @param error technical error details
     * @param <T> the type parameter
     * @return ApiResponse with error status
     */
    public static <T> ApiResponse<T> error(Integer status, String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a not found error response (404)
     *
     * @param message error message
     * @param <T> the type parameter
     * @return ApiResponse with 404 status
     */
    public static <T> ApiResponse<T> notFound(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(404)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates an unauthorized error response (401)
     *
     * @param message error message
     * @param <T> the type parameter
     * @return ApiResponse with 401 status
     */
    public static <T> ApiResponse<T> unauthorized(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(401)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates a forbidden error response (403)
     *
     * @param message error message
     * @param <T> the type parameter
     * @return ApiResponse with 403 status
     */
    public static <T> ApiResponse<T> forbidden(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(403)
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * Creates an internal server error response (500)
     *
     * @param message error message
     * @param error technical error details
     * @param <T> the type parameter
     * @return ApiResponse with 500 status
     */
    public static <T> ApiResponse<T> serverError(String message, String error) {
        return ApiResponse.<T>builder()
                .success(false)
                .status(500)
                .message(message)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    // ========================================
    // Utility Methods
    // ========================================

    /**
     * Adds request path to the response
     *
     * @param path the request path
     * @return this ApiResponse for method chaining
     */
    public ApiResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * Gets formatted timestamp as LocalDateTime
     *
     * @return LocalDateTime representation of timestamp
     */
    public LocalDateTime getTimestampAsDateTime() {
        if (timestamp == null) {
            return null;
        }
        return LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(timestamp),
                ZoneId.systemDefault()
        );
    }
}
