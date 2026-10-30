package com.whoami.launch.service.impl;

import com.whoami.launch.dto.ReelLikeResponse;
import com.whoami.launch.entity.ReelLike;
import com.whoami.launch.repository.ReelLikeRepository;
import com.whoami.launch.service.ReelLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReelLikeServiceImpl implements ReelLikeService {

    private final ReelLikeRepository reelLikeRepository;

    @Override
    public ReelLikeResponse toggleLike(
            String userId,
            String reelId) {

        boolean alreadyLiked =
                reelLikeRepository.existsByUserIdAndReelId(
                        userId,
                        reelId
                );

        if (alreadyLiked) {

            reelLikeRepository.deleteByUserIdAndReelId(
                    userId,
                    reelId
            );

        } else {

            ReelLike reelLike = new ReelLike();

            reelLike.setUserId(userId);
            reelLike.setReelId(reelId);

            reelLikeRepository.save(reelLike);
        }

        long totalLikes =
                reelLikeRepository.countByReelId(reelId);

        boolean likedByCurrentUser =
                reelLikeRepository.existsByUserIdAndReelId(
                        userId,
                        reelId
                );

        return new ReelLikeResponse(
                reelId,
                totalLikes,
                likedByCurrentUser
        );
    }

    @Override
    @Transactional(readOnly = true)
    public long getLikeCount(String reelId) {

        return reelLikeRepository.countByReelId(reelId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isLiked(
            String userId,
            String reelId) {

        return reelLikeRepository.existsByUserIdAndReelId(
                userId,
                reelId
        );
    }
}
