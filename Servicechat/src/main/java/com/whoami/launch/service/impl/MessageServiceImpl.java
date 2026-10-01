package com.whoami.launch.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.chat.ChatNotificationEvent;
import com.whoami.launch.entities.Conversation;
import com.whoami.launch.entities.Message;
import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.enums.MessageType;
import com.whoami.launch.payload.ChatMessageDTO;
import com.whoami.launch.repositories.ConversationRepository;
import com.whoami.launch.repositories.MessageRepository;
import com.whoami.launch.service.MessageService;
import com.whoami.launch.service.NotificationProducer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationProducer notificationProducer;

    @Override
    @Transactional
    public ChatMessageDTO saveMessage(
            ChatMessageDTO payload) {

        try {

            List<String> participants =
                    Arrays.asList(
                            payload.getSenderId(),
                            payload.getReceiverId());

            participants.sort(String::compareTo);

            String conversationId =
                    participants.get(0)
                            + "_"
                            + participants.get(1);

            Conversation conversation =
                    conversationRepository
                            .findByConversationId(
                                    conversationId)
                            .orElse(null);

            if (conversation == null) {

                conversation = new Conversation();

                conversation.setConversationId(
                        conversationId);

                conversation.setParticipants(
                        new ArrayList<>(participants));

                conversation.setSenderId(
                        payload.getSenderId());

                conversation.setReceiverId(
                        payload.getReceiverId());

                conversation.setMessages(
                        new ArrayList<>());

                conversation =
                        conversationRepository.save(
                                conversation);
            }

            Message message = new Message();

            message.setSenderId(
                    payload.getSenderId());

            message.setReceiverId(
                    payload.getReceiverId());

            message.setContent(
                    payload.getText());

            message.setTimestamp(
                    LocalDateTime.now());

            message.setRead(false);

            message.setStatus(
                    MessageStatus.SENT);

            /*
             * Reply support
             */
            message.setReplyToMessageId(
                    payload.getReplyToMessageId());

            message.setReplyPreview(
                    payload.getReplyPreview());

            message.setReplySenderId(
                    payload.getReplySenderId());

            message.setMessageType(
                    payload.getMessageType() != null
                            ? payload.getMessageType()
                            : MessageType.TEXT);

            /*
             * Media support
             */
            message.setMediaUrl(
                    payload.getMediaUrl());

            message.setFileName(
                    payload.getFileName());

            message.setMimeType(
                    payload.getMimeType());

            message.setFileSize(
                    payload.getFileSize());

            message.setReferenceId(
                    payload.getReferenceId());

            message.setConversation(
                    conversation);

            conversation.getMessages()
                    .add(message);

            conversation.setLastMessage(
                    payload.getText());

            conversation.setLastMessageTimestamp(
                    message.getTimestamp());

            conversationRepository.save(
                    conversation);

            Message savedMessage =
                    messageRepository.save(message);

            notifyReceiver(savedMessage);

            return mapToDTO(savedMessage);

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to save message: "
                            + e.getMessage(),
                    e);
        }
    }

    @Override
    @Transactional
    public ChatMessageDTO saveAndSend(
            ChatMessageDTO payload) {

        ChatMessageDTO saved =
                saveMessage(payload);

        messagingTemplate.convertAndSend(
                "/topic/messages/"
                        + saved.getReceiverId(),
                saved);

        messagingTemplate.convertAndSend(
                "/topic/messages/"
                        + saved.getSenderId(),
                saved);

        return saved;
    }

    @Override
    @Transactional
    public MessageStatus updateStatus(
            String messageId,
            MessageStatus incomingStatus) {

        Message message =
                messageRepository
                        .findByMessageId(messageId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Message not found: "
                                                + messageId));

        MessageStatus currentStatus =
                message.getStatus();

        if (currentStatus == MessageStatus.READ) {
            return currentStatus;
        }

        if (currentStatus == MessageStatus.DELIVERED
                && incomingStatus == MessageStatus.SENT) {

            return currentStatus;
        }

        if (incomingStatus == MessageStatus.READ) {

            message.setStatus(
                    MessageStatus.READ);

            message.setRead(true);

            messageRepository.save(message);

            return MessageStatus.READ;
        }

        if (incomingStatus == MessageStatus.DELIVERED
                && currentStatus == MessageStatus.SENT) {

            message.setStatus(
                    MessageStatus.DELIVERED);

            messageRepository.save(message);

            return MessageStatus.DELIVERED;
        }

        return currentStatus;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ChatMessageDTO> getMessages(
            String conversationId,
            Pageable pageable) {

        return messageRepository
                .findByConversation_ConversationId(
                        conversationId,
                        pageable)
                .map(this::mapToDTO);
    }

    private void notifyReceiver(
            Message message) {

        ChatNotificationEvent event =
                new ChatNotificationEvent();

        event.setSenderId(
                message.getSenderId());

        event.setReceiverId(
                message.getReceiverId());

        if (message.getConversation() != null) {

            event.setConversationId(
                    message.getConversation()
                            .getConversationId());
        }

        event.setMessagePreview(
                message.getContent());

        notificationProducer
                .sendNotification(event);
    }

    @Override
    @Transactional
    public ChatMessageDTO createSystemMessage(
            String senderId,
            String receiverId,
            MessageType type,
            String title,
            String referenceId,
            String imageUrl,
            String metadataJson) {

        ChatMessageDTO dto =
                new ChatMessageDTO();

        dto.setSenderId(senderId);
        dto.setReceiverId(receiverId);
        dto.setText(title);
        dto.setMessageType(type);
        dto.setReferenceId(referenceId);
        dto.setMediaUrl(imageUrl);
        dto.setMetadataJson(metadataJson);

        return saveMessage(dto);
    }

    private ChatMessageDTO mapToDTO(
            Message message) {

        ChatMessageDTO dto =
                new ChatMessageDTO();

        dto.setId(message.getMessageId());
        dto.setSenderId(message.getSenderId());
        dto.setReceiverId(message.getReceiverId());
        dto.setText(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setIsRead(message.isRead());
        dto.setStatus(message.getStatus());
        dto.setMessageType(message.getMessageType());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setFileName(message.getFileName());
        dto.setMimeType(message.getMimeType());
        dto.setFileSize(message.getFileSize());
        dto.setReferenceId(message.getReferenceId());
        dto.setReplyToMessageId(
                message.getReplyToMessageId());
        dto.setReplyPreview(
                message.getReplyPreview());
        dto.setReplySenderId(
                message.getReplySenderId());

        return dto;
    }
}