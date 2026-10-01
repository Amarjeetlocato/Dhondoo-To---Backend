package com.whoami.launch.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReviewRequest;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ReviewStatsResponse;
import com.whoami.launch.entity.Review;
import com.whoami.launch.enums.ReviewStatus;
import com.whoami.launch.enums.ReviewTargetType;
import com.whoami.launch.enums.VerificationType;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReviewRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.ReviewService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public ReviewResponse createReview(
            String userId,
            ReviewRequest request) {

        validateTarget(
                request.getTargetType(),
                request.getTargetId());

        reviewRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId,
                        request.getTargetType(),
                        request.getTargetId())
                .ifPresent(review -> {
                    throw new RuntimeException(
                            "Review already exists");
                });

        Review review = new Review();

        review.setUserId(userId);
        review.setTargetType(request.getTargetType());
        review.setTargetId(request.getTargetId());
        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());
        review.setStatus(ReviewStatus.ACTIVE);
        review.setVerificationType(VerificationType.NONE);

        return mapToResponse(
                reviewRepository.save(review));
    }

    @Override
    public ReviewResponse updateReview(
            String userId,
            String reviewId,
            ReviewRequest request) {

        Review review =
                reviewRepository.findByReviewId(reviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException(
                    "You can update only your own review");
        }

        review.setRating(request.getRating());
        review.setReviewText(request.getReviewText());

        return mapToResponse(
                reviewRepository.save(review));
    }

    @Override
    public void deleteReview(
            String userId,
            String reviewId) {

        Review review =
                reviewRepository.findByReviewId(reviewId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Review not found"));

        if (!review.getUserId().equals(userId)) {
            throw new RuntimeException(
                    "You can delete only your own review");
        }

        review.setStatus(ReviewStatus.DELETED);

        reviewRepository.save(review);
    }

    @Override
    public PageResponse<ReviewResponse> getReviews(
            ReviewTargetType targetType,
            String targetId,
            int page,
            int size) {

        Page<Review> reviewPage =
                reviewRepository
                        .findByTargetTypeAndTargetIdAndStatus(
                                targetType,
                                targetId,
                                ReviewStatus.ACTIVE,
                                PageRequest.of(page, size));

        List<ReviewResponse> content =
                reviewPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                content,
                reviewPage.getNumber(),
                reviewPage.getSize(),
                reviewPage.getTotalElements(),
                reviewPage.getTotalPages(),
                reviewPage.isFirst(),
                reviewPage.isLast()
        );
    }

    @Override
    public ReviewStatsResponse getReviewStats(
            ReviewTargetType targetType,
            String targetId) {

        Double avg =
                reviewRepository.getAverageRating(
                        targetType,
                        targetId);

        long total =
                reviewRepository
                        .countByTargetTypeAndTargetIdAndStatus(
                                targetType,
                                targetId,
                                ReviewStatus.ACTIVE);

        ReviewStatsResponse response =
                new ReviewStatsResponse();

        response.setTargetId(targetId);
        response.setAverageRating(
                avg == null ? 0.0 : avg);
        response.setTotalReviews(total);

        return response;
    }

    @Override
    public void verifyProductReview(
            String userId,
            String productId,
            String orderId) {

        reviewRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId,
                        ReviewTargetType.PRODUCT,
                        productId)
                .ifPresent(review -> {

                    review.setVerificationType(
                            VerificationType.PURCHASE);

                    review.setVerifiedOrderId(orderId);

                    reviewRepository.save(review);
                });
    }

    @Override
    public void verifyServiceReview(
            String userId,
            String serviceId,
            String bookingId) {

        reviewRepository
                .findByUserIdAndTargetTypeAndTargetId(
                        userId,
                        ReviewTargetType.SERVICE,
                        serviceId)
                .ifPresent(review -> {

                    review.setVerificationType(
                            VerificationType.BOOKING);

                    review.setVerifiedBookingId(bookingId);

                    reviewRepository.save(review);
                });
    }

    private void validateTarget(
            ReviewTargetType targetType,
            String targetId) {

        switch (targetType) {

            case PRODUCT:
                productRepository.findByProductId(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));
                break;

            case SERVICE:
                serviceRepository.findByServiceId(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service not found"));
                break;

            default:
                throw new RuntimeException(
                        "Invalid review target type");
        }
    }

    private ReviewResponse mapToResponse(
            Review review) {

        ReviewResponse response =
                new ReviewResponse();

        response.setReviewId(review.getReviewId());
        response.setUserId(review.getUserId());
        response.setTargetType(review.getTargetType());
        response.setTargetId(review.getTargetId());
        response.setRating(review.getRating());
        response.setReviewText(review.getReviewText());
        response.setVerificationType(
                review.getVerificationType());
        response.setStatus(review.getStatus());
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());

        return response;
    }
}