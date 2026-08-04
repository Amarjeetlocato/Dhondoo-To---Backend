package com.whoami.launch.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.whoami.launch.payload.ChatMessageDTO;
import com.whoami.launch.service.MessageService;

@RestController
@RequestMapping("/api/chat")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<ChatMessageDTO> postMessage(@RequestBody ChatMessageDTO payload,
            Principal principal) {

        if (principal != null) {
            payload.setSenderId(principal.getName());
        }

        ChatMessageDTO saved = messageService.saveAndSend(payload);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Page<ChatMessageDTO>> getConversationMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ChatMessageDTO> messages = messageService.getMessages(conversationId, pageable);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(
            Map.of(
                "status", "UP",
                "service", "gateway"
            )
        );
    }
}
