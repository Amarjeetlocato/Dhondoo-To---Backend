package com.whoami.launch.service;

import com.whoami.launch.dto.ReelLikeResponse;

public interface ReelLikeService {

    ReelLikeResponse toggleLike(
            String userId,
            String reelId);

    long getLikeCount(
            String reelId);

    boolean isLiked(
            String userId,
            String reelId);
}