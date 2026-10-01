package com.whoami.launch.dto;

import com.whoami.launch.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelCommentResponse {

    private String commentId;

    private String reelId;
    private String userId;

    private String comment;

    private CommentStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
