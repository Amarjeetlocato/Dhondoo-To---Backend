package com.whoami.launch.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.validation.FieldError;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.whoami.launch.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= VALIDATION =================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(
            MethodArgumentNotValidException ex
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getAllErrors()
                .forEach(error -> {

                    String field =
                            ((FieldError) error).getField();

                    String message =
                            error.getDefaultMessage();

                    errors.put(field, message);
                });

        ApiResponse<Map<String, String>> response =
                new ApiResponse<>(
                        false,
                        "Validation failed",
                        errors
                );

        return ResponseEntity
                .badRequest()
                .body(response);
    }

    // ================= USER NOT FOUND =================

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(
            UserNotFoundException ex
    ) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(
                        new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // ================= USER EXISTS =================

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleUserExists(
            UserAlreadyExistsException ex
    ) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                        new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // ================= INVALID OTP =================

    @ExceptionHandler(InvalidOtpException.class)
    public ResponseEntity<?> handleInvalidOtp(
            InvalidOtpException ex
    ) {

        return ResponseEntity.badRequest()
                .body(
                        new ApiResponse<>(
                                false,
                                ex.getMessage(),
                                null
                        )
                );
    }

    // ================= BAD CREDENTIALS =================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(
            BadCredentialsException ex
    ) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(
                        new ApiResponse<>(
                                false,
                                "Invalid credentials, please register.",
                                null
                        )
                );
    }

    // ================= GENERIC =================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(
            Exception ex
    ) {

        System.err.println("============== ERROR ==============");
        ex.printStackTrace();
        System.err.println("===================================");

        return ResponseEntity.status(
                HttpStatus.INTERNAL_SERVER_ERROR
        ).body(
                new ApiResponse<>(
                        false,
                        ex.getClass().getName() + " : " + ex.getMessage(),
                        null
                )
        );
    }
}