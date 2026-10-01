package com.whoami.launch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LocationResponseDTO {

    private String locationId;
    private String userId;

    private Double latitude;
    private Double longitude;

    private LocalDateTime timestamp;
}
