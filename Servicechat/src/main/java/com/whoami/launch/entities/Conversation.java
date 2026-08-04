package com.whoami.launch.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @Id
    private String id;

    @Column(name = "conversation_id", unique = true)
    private String conversationId;

    private String senderId;

    private String receiverId;

        @OneToMany(
            mappedBy = "conversation",
            cascade = CascadeType.ALL
        )
        private List<Message> messages = new ArrayList<>();

        @ElementCollection
        private List<String> participants = new ArrayList<>();

        private String lastMessage;

        private java.time.LocalDateTime lastMessageTimestamp;

}