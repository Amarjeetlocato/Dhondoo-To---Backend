package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.BusinessType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBusinessApplicationRequest {

    private BusinessType businessType;

    @Size(max = 150)
    private String businessName;

    @Size(max = 1000)
    private String description;

    @Size(max = 30)
    private String phone;

    @Email
    @Size(max = 150)
    private String email;

    @Size(max = 500)
    private String address;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal latitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal longitude;
}