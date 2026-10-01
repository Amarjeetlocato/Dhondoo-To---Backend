package com.whoami.launch.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.enums.MessageType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "chat_message")
public class Message {

    /**
     * Internal database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public message identity.
     * Example: MESSAGE_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String messageId;

    /**
     * User Registry identity of sender.
     */
    @Column(nullable = false, length = 30)
    private String senderId;

    /**
     * User Registry identity of receiver.
     */
    @Column(nullable = false, length = 30)
    private String receiverId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String metadataJson;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageStatus status = MessageStatus.SENT;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    /**
     * Message being replied to.
     * Contains MESSAGE_... identity.
     */
    @Column(length = 30)
    private String replyToMessageId;

    @Column(columnDefinition = "TEXT")
    private String replyPreview;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;

    /**
     * IMAGE / FILE media.
     */
    private String mediaUrl;

    private String fileName;

    private String mimeType;

    private Long fileSize;

    /**
     * Reference to another domain entity.
     *
     * Examples:
     * PRODUCT_...
     * SERVICE_...
     * REEL_...
     * BUSINESS_...
     * ORDER_...
     */
    @Column(length = 50)
    private String referenceId;

    private String replySenderId;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @PrePersist
    protected void prePersist() {

        if (messageId == null) {
            messageId = "MESSAGE_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }
}