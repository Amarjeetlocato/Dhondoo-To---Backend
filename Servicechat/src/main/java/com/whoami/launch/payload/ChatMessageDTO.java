package com.whoami.launch.payload;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.whoami.launch.enums.MessageStatus;
import com.whoami.launch.enums.MessageType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChatMessageDTO {

    private String id;

    private String senderId;

    private String receiverId;

    @JsonProperty("text")
    @JsonAlias("content")
    private String text;

    private String metadataJson;
    private LocalDateTime timestamp;

    private Boolean isRead = false;

    private MessageStatus status;
    
    private MessageType messageType;

    private String mediaUrl;

    private String fileName;

    private String mimeType;

    private Long fileSize;

    private String referenceId;
   

    /*
     * Reply Fields
     */
    private String replyToMessageId;

    private String replyPreview;

    private String replySenderId;

    
    @JsonCreator
    public ChatMessageDTO(
            @JsonProperty("id") String id,
            @JsonProperty("senderId") String senderId,
            @JsonProperty("receiverId") String receiverId,
            @JsonProperty("text") String text,
            @JsonProperty("timestamp") LocalDateTime timestamp,
            @JsonProperty("isRead") Boolean isRead,
            @JsonProperty("status") MessageStatus status,

            @JsonProperty("messageType") MessageType messageType,
            @JsonProperty("mediaUrl") String mediaUrl,
            @JsonProperty("fileName") String fileName,
            @JsonProperty("mimeType") String mimeType,
            @JsonProperty("fileSize") Long fileSize,
            @JsonProperty("referenceId") String referenceId,

            @JsonProperty("replyToMessageId") String replyToMessageId,
            @JsonProperty("replyPreview") String replyPreview,
            @JsonProperty("replySenderId") String replySenderId) {

        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.text = text;
        this.timestamp = timestamp;

        this.isRead = isRead != null ? isRead : false;
        this.status = status;

        this.messageType = messageType;
        this.mediaUrl = mediaUrl;
        this.fileName = fileName;
        this.mimeType = mimeType;
        this.fileSize = fileSize;
        this.referenceId = referenceId;

        this.replyToMessageId = replyToMessageId;
        this.replyPreview = replyPreview;
        this.replySenderId = replySenderId;
    }
    

}