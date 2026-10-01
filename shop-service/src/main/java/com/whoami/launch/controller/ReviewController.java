package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReviewRequest;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ReviewStatsResponse;
import com.whoami.launch.enums.ReviewTargetType;
import com.whoami.launch.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping("/users/{userId}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable String userId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.createReview(userId, request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Review created successfully",
                        response));
    }

    @PutMapping("/users/{userId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable String userId,
            @PathVariable String reviewId,
            @Valid @RequestBody ReviewRequest request) {

        ReviewResponse response =
                reviewService.updateReview(
                        userId,
                        reviewId,
                        request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Review updated successfully",
                        response));
    }

    @DeleteMapping("/users/{userId}/reviews/{reviewId}")
    public ResponseEntity<ApiResponse<String>> deleteReview(
            @PathVariable String userId,
            @PathVariable String reviewId) {

        reviewService.deleteReview(
                userId,
                reviewId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Review deleted successfully",
                        "SUCCESS"));
    }

    @GetMapping("/reviews")
    public ResponseEntity<ApiResponse<PageResponse<ReviewResponse>>> getReviews(
            @RequestParam ReviewTargetType targetType,
            @RequestParam String targetId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ReviewResponse> response =
                reviewService.getReviews(
                        targetType,
                        targetId,
                        page,
                        size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Reviews fetched successfully",
                        response));
    }

    @GetMapping("/reviews/stats")
    public ResponseEntity<ApiResponse<ReviewStatsResponse>> getReviewStats(
            @RequestParam ReviewTargetType targetType,
            @RequestParam String targetId) {

        ReviewStatsResponse response =
                reviewService.getReviewStats(
                        targetType,
                        targetId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Review stats fetched successfully",
                        response));
    }

    @PostMapping("/internal-api/reviews/verify/product")
    public ResponseEntity<ApiResponse<String>> verifyProductReview(
            @RequestParam String userId,
            @RequestParam String productId,
            @RequestParam String orderId) {

        reviewService.verifyProductReview(
                userId,
                productId,
                orderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product review verified",
                        "SUCCESS"));
    }

    @PostMapping("/internal-api/reviews/verify/service")
    public ResponseEntity<ApiResponse<String>> verifyServiceReview(
            @RequestParam String userId,
            @RequestParam String serviceId,
            @RequestParam String bookingId) {

        reviewService.verifyServiceReview(
                userId,
                serviceId,
                bookingId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Service review verified",
                        "SUCCESS"));
    }
}