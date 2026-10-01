package com.whoami.launch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServiceResponseDTO {

    private String serviceId;

    private String businessId;
    private String businessName;

    private String serviceName;

    private Double price;
    private String serviceDescription;

    private String duration;
    private String orderType;

    private String suggestion;
    private String visibility;
    private String badges;

    private String thumbnailUrl;
    private String thumbnailPublicId;

    private String promoVideoUrl;
    private String videoPublicId;
}
