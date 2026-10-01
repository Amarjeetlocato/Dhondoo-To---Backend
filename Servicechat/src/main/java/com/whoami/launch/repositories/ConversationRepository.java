package com.whoami.launch.repositories;

import com.whoami.launch.entities.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByConversationId(
            String conversationId);

    List<Conversation> findByParticipantsContaining(
            String participant);

    List<Conversation> findBySenderIdOrReceiverId(
            String senderId,
            String receiverId);
}