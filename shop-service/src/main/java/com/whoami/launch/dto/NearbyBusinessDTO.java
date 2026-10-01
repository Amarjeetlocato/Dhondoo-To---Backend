package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyBusinessDTO {

    private String businessId;
    private String businessName;
    private String userId;

    private String address;

    private Double latitude;
    private Double longitude;

    private Double distance;

    private String mobileNumber;
}
