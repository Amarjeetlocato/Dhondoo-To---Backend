package com.whoami.launch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FollowShopResponse {

    private String shopId;

    private Long followersCount;

    private Boolean following;
}