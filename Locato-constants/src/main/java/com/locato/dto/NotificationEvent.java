package com.locato.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    private String title;

    private String message;

    private String imageUrl;

    private String targetId;

    private String targetType;

    private String type;

    private Boolean sendPush;

    private String metadataJson;

    private String actionsJson;

    private String deepLink;
}
