package com.whoami.launch.exception;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntime(
            RuntimeException ex) {

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .errorCode("BUSINESS_ERROR")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex) {

        String errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .success(false)
                        .message(errors)
                        .errorCode("VALIDATION_ERROR")
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex) {

        ex.printStackTrace();

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message("Something went wrong")
                                .errorCode("INTERNAL_SERVER_ERROR")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
    
    @ExceptionHandler(AdminNotFoundException.class)
    public ResponseEntity<ErrorResponse> adminNotFound(
            AdminNotFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .errorCode("ADMIN_NOT_FOUND")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
    @ExceptionHandler(InvalidTotpException.class)
    public ResponseEntity<ErrorResponse> invalidTotp(
            InvalidTotpException ex) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .errorCode("INVALID_TOTP")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
    
    @ExceptionHandler(TotpNotConfiguredException.class)
    public ResponseEntity<ErrorResponse> totpNotConfigured(
            TotpNotConfiguredException ex) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(
                        ErrorResponse.builder()
                                .success(false)
                                .message(ex.getMessage())
                                .errorCode("TOTP_NOT_CONFIGURED")
                                .timestamp(LocalDateTime.now())
                                .build()
                );
    }
}