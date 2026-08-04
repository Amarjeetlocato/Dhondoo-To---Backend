package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerProfileResponseDTO {
    private String customerId;
    private String userId;
    private String username;
    private String email;
    private String logoUrl;
    private String bannerUrl;
}
