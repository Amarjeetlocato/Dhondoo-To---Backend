package com.whoami.launch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FollowBusinessRequest {

    @NotBlank(message = "Business id is required")
    private String businessId;

    @NotBlank(message = "User id is required")
    private String userId;
}
