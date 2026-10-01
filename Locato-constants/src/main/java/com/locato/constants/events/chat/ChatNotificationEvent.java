package com.locato.constants.events.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatNotificationEvent {

    private String senderId;
    private String receiverId;
    private String conversationId;

    private String senderName;
    private String senderImage;
    private String messagePreview;
}
