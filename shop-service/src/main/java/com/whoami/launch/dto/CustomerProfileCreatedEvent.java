package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder 
public class CustomerProfileCreatedEvent {

    private String userId;

    private String customerId;

    private String username;

    private String email;
}