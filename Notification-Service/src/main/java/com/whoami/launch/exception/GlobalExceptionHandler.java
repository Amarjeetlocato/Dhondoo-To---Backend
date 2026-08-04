package com.whoami.launch.exception;

import com.whoami.launch.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for all controllers
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        log.error("Resource not found: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, WebRequest request) {

        log.error("Validation error occurred");

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ApiResponse<Map<String, String>> response =
                ApiResponse.<Map<String, String>>builder()
                        .statusCode(HttpStatus.BAD_REQUEST.value())
                        .message("Validation failed")
                        .data(errors)
                        .success(false)
                        .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        log.error("Invalid argument: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid argument: " + ex.getMessage()
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle NumberFormatException
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ApiResponse<Object>> handleNumberFormatException(
            NumberFormatException ex, WebRequest request) {

        log.error("Number format error: {}", ex.getMessage());

        return new ResponseEntity<>(
                ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        "Invalid number format"
                ),
                HttpStatus.BAD_REQUEST
        );
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(
            Exception ex, WebRequest request) {

        log.error("Internal server error: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(
                ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An internal server error occurred"
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}