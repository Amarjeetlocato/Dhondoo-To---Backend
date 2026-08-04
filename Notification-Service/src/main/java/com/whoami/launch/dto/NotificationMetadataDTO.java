package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMetadataDTO {

    private String shopId;

    private String productId;

    private String serviceId;

    private String reelId;

    private String conversationId;

    private String orderId;
}