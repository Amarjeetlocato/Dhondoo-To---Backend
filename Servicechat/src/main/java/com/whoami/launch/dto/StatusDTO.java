package com.whoami.launch.dto;

import com.whoami.launch.enums.MessageStatus;

import lombok.Data;

@Data
public class StatusDTO {

    private String messageId;

    private String senderId;

    private String receiverId;

    private MessageStatus status;
}