package com.whoami.launch.payload;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypingDTO {

    @JsonProperty("senderId")
    private String senderId;

    @JsonProperty("receiverId")
    private String receiverId;

    @JsonProperty("isTyping")
    private Boolean isTyping;

    @JsonCreator
    public TypingDTO(
            @JsonProperty("senderId") String senderId,
            @JsonProperty("receiverId") String receiverId,
            @JsonProperty("isTyping") Boolean isTyping) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.isTyping = isTyping != null ? isTyping : false;
    }
}
