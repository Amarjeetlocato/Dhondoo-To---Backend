package com.whoami.launch.dto;

import java.time.LocalDateTime;

import com.whoami.launch.enums.ReviewTargetType;
import com.whoami.launch.enums.ReviewStatus;
import com.whoami.launch.enums.VerificationType;

import lombok.Data;

@Data
public class ReviewResponse {

    private Long id;

    private String userId;

    private ReviewTargetType targetType;

    private String targetId;

    private Integer rating;

    private String reviewText;

    private VerificationType verificationType;

    private ReviewStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Getters & Setters
}