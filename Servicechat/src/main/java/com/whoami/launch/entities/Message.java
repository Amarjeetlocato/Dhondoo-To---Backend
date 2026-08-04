package com.whoami.launch.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.enums.MessageType;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_message")
public class Message {

    @Id
    private String id;

    private String senderId;

    private String receiverId;

    @Column(columnDefinition = "TEXT")
    private String content;
    
    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    private MessageStatus status = MessageStatus.SENT;

    @Column(name = "is_read")
    private boolean isRead = false;

    /*
     * REPLY SUPPORT
     */
    private String replyToMessageId;

    @Column(columnDefinition = "TEXT")
    private String replyPreview;
    
    
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;

    /*
     * For IMAGE / FILE
     */
    private String mediaUrl;

    private String fileName;

    private String mimeType;

    private Long fileSize;

    /*
     * For PRODUCT / SERVICE / REEL / SHOP / ORDER
     */
    private String referenceId;
    
    private String replySenderId;


    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversation_id")
    private Conversation conversation;
}