package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyReelDTO {
    private String reelId;
    private String shopId;
    private String reelVideo;
    private String reelDescription;
    private Double distance;
}
