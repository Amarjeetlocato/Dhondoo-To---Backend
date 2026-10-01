package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelLikeResponse {

    private String reelId;

    private Long totalLikes;

    private boolean likedByCurrentUser;
}
