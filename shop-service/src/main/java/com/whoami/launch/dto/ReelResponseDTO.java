package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelResponseDTO {
    private String reelId;
    private String shopId;
    private String reelVideo;
    private String reelThumbnail;
    private String reelDescription;
    private String reelReviews;
    private Double reelRatings;
}
