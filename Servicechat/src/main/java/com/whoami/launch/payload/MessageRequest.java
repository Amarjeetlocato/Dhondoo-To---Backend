package com.whoami.launch.payload;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MessageRequest {
	
	private String content;
	private String sender;
	private String conversationId;
	private LocalDateTime MessageTime;

}
