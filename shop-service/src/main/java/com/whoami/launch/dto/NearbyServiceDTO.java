package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyServiceDTO {
    private String serviceId;
    private String serviceName;
    private String shopId;
    private Double price;
    private String serviceDescription;
    private Double distance;
}
