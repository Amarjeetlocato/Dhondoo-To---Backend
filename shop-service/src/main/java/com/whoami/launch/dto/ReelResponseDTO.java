package com.whoami.launch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelResponseDTO {

    private String reelId;

    private String businessId;
   

    private String reelVideo;
    private String reelThumbnail;
    private String reelDescription;

    private String reelReviews;
    private Double reelRatings;
}
