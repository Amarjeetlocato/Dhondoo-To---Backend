package com.whoami.launch.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.enums.MessageType;
import com.whoami.launch.payload.ChatMessageDTO;

public interface MessageService {

    ChatMessageDTO saveMessage(
            ChatMessageDTO payload);

    ChatMessageDTO saveAndSend(
            ChatMessageDTO payload);

    MessageStatus updateStatus(
            String messageId,
            MessageStatus incomingStatus);

    Page<ChatMessageDTO> getMessages(
            String conversationId,
            Pageable pageable);

    ChatMessageDTO createSystemMessage(
            String senderId,
            String receiverId,
            MessageType type,
            String title,
            String referenceId,
            String imageUrl,
            String metadataJson);
}