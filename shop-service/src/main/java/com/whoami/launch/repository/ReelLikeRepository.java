package com.whoami.launch.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.ReelLike;

@Repository
public interface ReelLikeRepository extends JpaRepository<ReelLike, Long> {

    Optional<ReelLike> findByUserIdAndReelId(
            String userId,
            String reelId);

    boolean existsByUserIdAndReelId(
    		String userId,
    		String reelId);

    long countByReelId(String reelId);

    void deleteByUserIdAndReelId(
    		String userId,
    		String reelId);
}