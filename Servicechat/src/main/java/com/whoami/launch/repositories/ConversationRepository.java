package com.whoami.launch.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entities.Conversation;

@Repository
public interface ConversationRepository
        extends JpaRepository<Conversation, String> {

    Conversation findByConversationId(String conversationId);

    List<Conversation> findByParticipantsContaining(String participant);

    List<Conversation> findBySenderIdOrReceiverId(String senderId, String receiverId);

}