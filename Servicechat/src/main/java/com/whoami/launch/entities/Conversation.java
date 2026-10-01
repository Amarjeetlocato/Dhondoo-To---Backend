package com.whoami.launch.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "conversation")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Conversation {

    /**
     * Internal database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public conversation identity.
     * Example: CONVERSATION_A7K92M4X
     */
    @Column(
            name = "conversation_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 35
    )
    private String conversationId;

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

    @OneToMany(
            mappedBy = "conversation",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Message> messages = new ArrayList<>();

    /**
     * USER_... identities participating in this conversation.
     */
    @ElementCollection
    private List<String> participants = new ArrayList<>();

    @Column(columnDefinition = "TEXT")
    private String lastMessage;

    private LocalDateTime lastMessageTimestamp;

    @PrePersist
    protected void prePersist() {

        if (conversationId == null) {
            conversationId = "CONVERSATION_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}