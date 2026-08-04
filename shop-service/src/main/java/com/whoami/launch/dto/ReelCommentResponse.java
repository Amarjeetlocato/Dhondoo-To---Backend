package com.whoami.launch.dto;

import java.time.LocalDateTime;

import com.whoami.launch.enums.CommentStatus;

import lombok.Data;

@Data
public class ReelCommentResponse {

    private Long id;

    private String reelId;

    private String userId;

    private String comment;

    private CommentStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Getters & Setters
}