package com.locato.dto;

import lombok.Data;

@Data
public class ChatNotificationEvent {

    private String senderId;
    private String receiverId;
    private String conversationId;

    private String senderName;
    private String senderImage;

    private String messagePreview;
}