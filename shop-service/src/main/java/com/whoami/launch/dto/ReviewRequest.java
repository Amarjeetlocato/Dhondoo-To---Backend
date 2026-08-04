package com.whoami.launch.dto;

import com.whoami.launch.enums.ReviewTargetType;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "Target type is required")
    private ReviewTargetType targetType;

    @NotNull(message = "Target id is required")
    private String targetId;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    @Size(max = 2000, message = "Review cannot exceed 2000 characters")
    private String reviewText;

    // Getters & Setters
}