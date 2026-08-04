package com.whoami.launch.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.ReelLikeResponse;
import com.whoami.launch.entity.ReelLike;
import com.whoami.launch.repository.ReelLikeRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.service.ReelLikeService;

@Service
public class ReelLikeService {

    @Autowired
    private ReelLikeRepository reelLikeRepository;

    @Autowired
    private ReelRepository reelRepository;

    
    public ReelLikeResponse toggleLike(
            String userId,
            String reelId) {

        reelRepository.findById(reelId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reel not found"));

        if (reelLikeRepository.existsByUserIdAndReelId(
                userId,
                reelId)) {

            reelLikeRepository.deleteByUserIdAndReelId(
                    userId,
                    reelId);

        } else {

            ReelLike like = new ReelLike();

            like.setUserId(userId);
            like.setReelId(reelId);

            reelLikeRepository.save(like);
        }

        ReelLikeResponse response =
                new ReelLikeResponse();

        response.setReelId(reelId);
        response.setTotalLikes(
                reelLikeRepository.countByReelId(reelId));

        response.setLikedByCurrentUser(
                reelLikeRepository.existsByUserIdAndReelId(
                        userId,
                        reelId));

        return response;
    }

    
    public long getLikeCount(String reelId) {
        return reelLikeRepository.countByReelId(reelId);
    }

    
    public boolean isLiked(
            String userId,
            String reelId) {

        return reelLikeRepository
                .existsByUserIdAndReelId(
                        userId,
                        reelId);
    }
}