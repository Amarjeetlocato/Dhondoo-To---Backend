package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ReelLikeResponse;
import com.whoami.launch.service.ReelLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ReelLikeController {

    private final ReelLikeService reelLikeService;

    @PostMapping("/users/{userId}/reels/{reelId}/like")
    public ResponseEntity<ApiResponse<ReelLikeResponse>> toggleLike(
            @PathVariable String userId,
            @PathVariable String reelId) {

        ReelLikeResponse response =
                reelLikeService.toggleLike(userId, reelId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Like status updated",
                        response));
    }

    @GetMapping("/reels/{reelId}/likes/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @PathVariable String reelId) {

        long count =
                reelLikeService.getLikeCount(reelId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Like count fetched",
                        count));
    }

    @GetMapping("/users/{userId}/reels/{reelId}/liked")
    public ResponseEntity<ApiResponse<Boolean>> isLiked(
            @PathVariable String userId,
            @PathVariable String reelId) {

        boolean liked =
                reelLikeService.isLiked(userId, reelId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Like status fetched",
                        liked));
    }
}
