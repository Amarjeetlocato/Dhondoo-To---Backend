package com.whoami.launch.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.whoami.launch.dto.StatusDTO;
import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.payload.ChatMessageDTO;
import com.whoami.launch.payload.TypingDTO;
import com.whoami.launch.service.MessageService;

@Controller
public class ChatController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping({"/messages", "/chat.send"})
    public void receiveChatMessage(@Payload ChatMessageDTO payload, Principal principal) {
        if (principal != null) {
            payload.setSenderId(principal.getName());
        }
        messageService.saveAndSend(payload);
    }

    @MessageMapping("/typing")
    public void handleTyping(TypingDTO dto, Principal principal) {
        if (principal != null) {
            dto.setSenderId(principal.getName());
        }
        if (dto.getReceiverId() != null) {
            // Broadcast typing status instantly to receiver
            messagingTemplate.convertAndSend("/topic/typing/" + dto.getReceiverId(), dto);
        }
    }
    
    @MessageMapping("/status")
    public void handleStatus(
            StatusDTO dto,
            Principal principal) {

        if (principal != null) {
            dto.setSenderId(principal.getName());
        }

        MessageStatus finalStatus =
                messageService.updateStatus(
                        dto.getMessageId(),
                        dto.getStatus()
                );

        dto.setStatus(finalStatus);

        System.out.println(
                "Broadcasting status => "
                        + dto.getMessageId()
                        + " : "
                        + finalStatus
                        + " to "
                        + dto.getReceiverId());

        messagingTemplate.convertAndSend(
                "/topic/status/" + dto.getReceiverId(),
                dto
        );
    }
    
    
}
