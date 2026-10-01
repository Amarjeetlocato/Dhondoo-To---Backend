package com.whoami.launch.service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReviewRequest;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ReviewStatsResponse;
import com.whoami.launch.enums.ReviewTargetType;

public interface ReviewService {

    ReviewResponse createReview(
            String userId,
            ReviewRequest request);

    ReviewResponse updateReview(
            String userId,
            String reviewId,
            ReviewRequest request);

    void deleteReview(
            String userId,
            String reviewId);

    PageResponse<ReviewResponse> getReviews(
            ReviewTargetType targetType,
            String targetId,
            int page,
            int size);

    ReviewStatsResponse getReviewStats(
            ReviewTargetType targetType,
            String targetId);

    void verifyProductReview(
            String userId,
            String productId,
            String orderId);

    void verifyServiceReview(
            String userId,
            String serviceId,
            String bookingId);
}