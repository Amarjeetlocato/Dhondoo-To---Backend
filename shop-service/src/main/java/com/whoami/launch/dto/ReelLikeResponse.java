package com.whoami.launch.dto;

import lombok.Data;

@Data
public class ReelLikeResponse {

    private String reelId;

    private Long totalLikes;

    private boolean likedByCurrentUser;

    // Getters & Setters
}