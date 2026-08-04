package com.whoami.launch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelCommentRequest;
import com.whoami.launch.dto.ReelCommentResponse;
import com.whoami.launch.service.ReelCommentService;

import jakarta.validation.Valid;

@RestController
public class ReelCommentController {

    @Autowired
    private ReelCommentService reelCommentService;

    @PostMapping("/api/users/{userId}/reels/comments")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> addComment(
            @PathVariable String userId,
            @Valid @RequestBody ReelCommentRequest request) {

        ReelCommentResponse response =
                reelCommentService.addComment(
                        userId,
                        request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment added successfully",
                        response));
    }

    @PutMapping("/api/users/{userId}/reels/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> updateComment(
            @PathVariable String userId,
            @PathVariable Long commentId,
            @Valid @RequestBody ReelCommentRequest request) {

        ReelCommentResponse response =
                reelCommentService.updateComment(
                        userId,
                        commentId,
                        request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment updated successfully",
                        response));
    }

    @DeleteMapping("/api/users/{userId}/reels/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable String userId,
            @PathVariable Long commentId) {

        reelCommentService.deleteComment(
                userId,
                commentId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment deleted successfully",
                        "SUCCESS"));
    }

    @GetMapping("/api/reels/{reelId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<ReelCommentResponse>>> getComments(
            @PathVariable String reelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ReelCommentResponse> response =
                reelCommentService.getComments(
                        reelId,
                        page,
                        size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comments fetched successfully",
                        response));
    }
}