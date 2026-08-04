package com.whoami.launch.dto;

import lombok.Data;

@Data
public class ReviewStatsResponse {

    private String targetId;

    private Double averageRating;

    private Long totalReviews;

    private Long fiveStarCount;

    private Long fourStarCount;

    private Long threeStarCount;

    private Long twoStarCount;

    private Long oneStarCount;

    // Getters & Setters
}