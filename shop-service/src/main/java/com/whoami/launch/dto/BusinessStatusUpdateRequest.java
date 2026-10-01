package com.whoami.launch.dto;

import com.locato.enums.BusinessStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BusinessStatusUpdateRequest {

    @NotNull(message = "Business status is required")
    private BusinessStatus businessStatus;
}
