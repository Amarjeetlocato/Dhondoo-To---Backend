package com.whoami.launch.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateNotificationRequest {

    private Long userId;
    private String title;
    private String message;
}