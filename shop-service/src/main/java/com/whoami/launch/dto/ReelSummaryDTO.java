package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ReelSummaryDTO {
    private String reelId;
    private String shopId;
    private String reelVideo;
    private String reelDescription;
}
