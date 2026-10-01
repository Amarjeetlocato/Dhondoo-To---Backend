package com.whoami.launch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerProfileResponseDTO {

    private String customerId;
    private String userId;

    private String fullName;
    private String email;

    private String logoUrl;
    private String bannerUrl;
}
