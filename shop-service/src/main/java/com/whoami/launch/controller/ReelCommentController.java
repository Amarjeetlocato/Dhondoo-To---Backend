package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelCommentRequest;
import com.whoami.launch.dto.ReelCommentResponse;
import com.whoami.launch.service.ReelCommentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReelCommentController {

    private final ReelCommentService reelCommentService;

    // ================= ADD COMMENT =================

    @PostMapping("/users/{userId}/reels/comments")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> addComment(
            @PathVariable String userId,
            @Valid @RequestBody ReelCommentRequest request) {

        ReelCommentResponse response =
                reelCommentService.addComment(
                        userId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment added successfully",
                        response
                )
        );
    }

    // ================= UPDATE COMMENT =================

    @PutMapping("/users/{userId}/reels/comments/{commentId}")
    public ResponseEntity<ApiResponse<ReelCommentResponse>> updateComment(
            @PathVariable String userId,
            @PathVariable String commentId,
            @Valid @RequestBody ReelCommentRequest request) {

        ReelCommentResponse response =
                reelCommentService.updateComment(
                        userId,
                        commentId,
                        request
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment updated successfully",
                        response
                )
        );
    }

    // ================= DELETE COMMENT =================

    @DeleteMapping("/users/{userId}/reels/comments/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable String userId,
            @PathVariable String commentId) {

        reelCommentService.deleteComment(
                userId,
                commentId
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comment deleted successfully",
                        "SUCCESS"
                )
        );
    }

    // ================= GET COMMENTS =================

    @GetMapping("/reels/{reelId}/comments")
    public ResponseEntity<ApiResponse<PageResponse<ReelCommentResponse>>>
    getComments(
            @PathVariable String reelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ReelCommentResponse> response =
                reelCommentService.getComments(
                        reelId,
                        page,
                        size
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Comments fetched successfully",
                        response
                )
        );
    }
}