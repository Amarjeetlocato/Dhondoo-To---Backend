package com.whoami.launch.service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelCommentRequest;
import com.whoami.launch.dto.ReelCommentResponse;

public interface ReelCommentService {

    ReelCommentResponse addComment(
            String userId,
            ReelCommentRequest request);

    ReelCommentResponse updateComment(
            String userId,
            String commentId,
            ReelCommentRequest request);

    void deleteComment(
            String userId,
            String commentId);

    PageResponse<ReelCommentResponse> getComments(
            String reelId,
            int page,
            int size);
}