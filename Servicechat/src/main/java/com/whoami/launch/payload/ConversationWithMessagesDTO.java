package com.whoami.launch.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConversationWithMessagesDTO {

    private String id;

    private List<String> participants;

    private String lastMessage;

    private LocalDateTime lastMessageTimestamp;

    private List<ChatMessageDTO> messages;

}
