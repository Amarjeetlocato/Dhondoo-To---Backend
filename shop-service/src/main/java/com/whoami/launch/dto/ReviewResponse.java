package com.whoami.launch.dto;

import com.whoami.launch.enums.ReviewStatus;
import com.whoami.launch.enums.ReviewTargetType;
import com.whoami.launch.enums.VerificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private String reviewId;

    private String userId;

    private ReviewTargetType targetType;
    private String targetId;

    private Integer rating;
    private String reviewText;

    private VerificationType verificationType;

    private ReviewStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
