package com.whoami.launch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReelCommentRequest {

    @NotNull(message = "Reel id is required")
    private String reelId;

    @NotBlank(message = "Comment cannot be empty")
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    // Getters & Setters
}