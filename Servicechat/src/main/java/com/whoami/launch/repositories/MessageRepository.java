package com.whoami.launch.repositories;

import com.whoami.launch.entities.Message;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MessageRepository
        extends JpaRepository<Message, Long> {

    Page<Message> findByConversation_ConversationId(
            String conversationId,
            Pageable pageable);

    Optional<Message> findByMessageId(
            String messageId);
}