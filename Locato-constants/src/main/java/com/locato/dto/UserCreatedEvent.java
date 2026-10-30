package com.locato.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String email;
    private String name;
    private String role;
    private LocalDateTime createdAt;
}
