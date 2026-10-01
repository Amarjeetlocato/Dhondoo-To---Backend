package com.whoami.launch.repository;

import com.whoami.launch.entity.Review;
import com.whoami.launch.enums.ReviewStatus;
import com.whoami.launch.enums.ReviewTargetType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    Optional<Review> findByUserIdAndTargetTypeAndTargetId(
            String userId,
            ReviewTargetType targetType,
            String targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(
            String userId,
            ReviewTargetType targetType,
            String targetId);

    Page<Review> findByTargetTypeAndTargetIdAndStatus(
            ReviewTargetType targetType,
            String targetId,
            ReviewStatus status,
            Pageable pageable);

    long countByTargetTypeAndTargetIdAndStatus(
            ReviewTargetType targetType,
            String targetId,
            ReviewStatus status);

    @Query("""
            SELECT AVG(r.rating)
            FROM Review r
            WHERE r.targetType = :targetType
            AND r.targetId = :targetId
            AND r.status = 'ACTIVE'
            """)
    Double getAverageRating(
            ReviewTargetType targetType,
            String targetId);

	Optional<Review> findByReviewId(String reviewId);
}