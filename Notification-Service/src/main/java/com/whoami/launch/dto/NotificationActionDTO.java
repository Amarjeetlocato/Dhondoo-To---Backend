package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationActionDTO {

    private String key;

    private String label;

    private String type;

    private String endpoint;
    private String method;
    private Boolean authenticationRequired;
}