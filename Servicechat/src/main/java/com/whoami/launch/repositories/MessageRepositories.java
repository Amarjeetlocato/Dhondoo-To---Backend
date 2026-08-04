package com.whoami.launch.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entities.Message;

@Repository
public interface MessageRepositories extends JpaRepository<Message, String> {

	Page<Message> findByConversation_ConversationId(String conversationId, Pageable pageable);

}
