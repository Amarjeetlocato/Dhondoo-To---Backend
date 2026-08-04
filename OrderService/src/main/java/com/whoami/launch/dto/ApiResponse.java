package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard API Response wrapper for all endpoints
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    
    private Integer statusCode;

    
    private String message;

   
    private T data;

   
    private Boolean success;

    /**
     * Build a successful response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message("Success")
                .data(data)
                .success(true)
                .build();
    }

    /**
     * Build a successful response with custom message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .statusCode(200)
                .message(message)
                .data(data)
                .success(true)
                .build();
    }

    /**
     * Build an error response
     */
    public static <T> ApiResponse<T> error(Integer statusCode, String message) {
        return ApiResponse.<T>builder()
                .statusCode(statusCode)
                .message(message)
                .success(false)
                .build();
    }

    /**
     * Build a created response (201)
     */
    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder()
                .statusCode(201)
                .message("Created successfully")
                .data(data)
                .success(true)
                .build();
    }
}
