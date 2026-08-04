package com.whoami.launch.controller;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.entities.Conversation;
import com.whoami.launch.payload.ChatMessageDTO;
import com.whoami.launch.payload.ConversationDTO;
import com.whoami.launch.payload.ConversationWithMessagesDTO;
import com.whoami.launch.repositories.ConversationRepository;

@RestController
@RequestMapping("/api/chat")
public class ConversationController {

    @Autowired
    private ConversationRepository conversationRepository;

    @GetMapping("/conversations")
    public ResponseEntity<List<ConversationDTO>> listConversations(
            Principal principal) {

        String userId = principal.getName();

        List<Conversation> conversations =
                conversationRepository.findByParticipantsContaining(userId);

        List<ConversationDTO> dtos =
                conversations.stream()
                        .map(c -> new ConversationDTO(
                                c.getConversationId(),
                                c.getParticipants(),
                                c.getLastMessage(),
                                c.getLastMessageTimestamp()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/users/{userId:.+}/conversations")
    public ResponseEntity<List<ConversationWithMessagesDTO>>
    listConversationsByUser(@PathVariable String userId) {

        List<Conversation> conversations =
                conversationRepository.findByParticipantsContaining(userId);

        List<ConversationWithMessagesDTO> dtos =
                conversations.stream().map(c -> {

                    List<ChatMessageDTO> messages =
                            c.getMessages().stream()
                                    .map(m -> new ChatMessageDTO(
                                    		 m.getId(),
                                             m.getSenderId(),
                                             m.getReceiverId(),
                                             m.getContent(),
                                             m.getTimestamp(),
                                             m.isRead(),
                                             m.getStatus(),
                                             m.getMessageType(),
                                             m.getMediaUrl(),
                                             m.getFileName(),
                                             m.getMimeType(),
                                             m.getFileSize(),
                                             m.getReferenceId(),
                                             m.getReplyToMessageId(),
                                             m.getReplyPreview(),
                                             m.getReplySenderId()
                                    ))
                                    .collect(Collectors.toList());

                    return new ConversationWithMessagesDTO(
                            c.getConversationId(),
                            c.getParticipants(),
                            c.getLastMessage(),
                            c.getLastMessageTimestamp(),
                            messages
                    );

                }).collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/users/{userId:.+}/conversations/summary")
    public ResponseEntity<List<ConversationDTO>>
    listConversationSummariesByUser(
            @PathVariable String userId) {

        List<Conversation> conversations =
                conversationRepository.findByParticipantsContaining(userId);

        List<ConversationDTO> dtos =
                conversations.stream()
                        .map(c -> new ConversationDTO(
                                c.getConversationId(),
                                c.getParticipants(),
                                c.getLastMessage(),
                                c.getLastMessageTimestamp()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/conversations/user/{userId}")
    public ResponseEntity<List<ConversationDTO>>
    getUserConversations(
            @PathVariable String userId) {

        List<Conversation> conversations =
                conversationRepository.findBySenderIdOrReceiverId(
                        userId,
                        userId);

        List<ConversationDTO> dtos =
                conversations.stream()
                        .map(c -> new ConversationDTO(
                                c.getConversationId(),
                                c.getParticipants(),
                                c.getLastMessage(),
                                c.getLastMessageTimestamp()))
                        .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }
}