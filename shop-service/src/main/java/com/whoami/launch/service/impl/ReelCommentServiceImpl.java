package com.whoami.launch.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelCommentRequest;
import com.whoami.launch.dto.ReelCommentResponse;
import com.whoami.launch.entity.ReelComment;
import com.whoami.launch.enums.CommentStatus;
import com.whoami.launch.repository.ReelCommentRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.service.ReelCommentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReelCommentServiceImpl
        implements ReelCommentService {

    private final ReelCommentRepository reelCommentRepository;
    private final ReelRepository reelRepository;

    @Override
    public ReelCommentResponse addComment(
            String userId,
            ReelCommentRequest request) {

        reelRepository.findByReelId(request.getReelId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reel not found: "
                                        + request.getReelId()
                        ));

        ReelComment comment =
                new ReelComment();

        comment.setUserId(userId);
        comment.setReelId(request.getReelId());
        comment.setComment(request.getComment());
        comment.setStatus(CommentStatus.ACTIVE);

        return mapToResponse(
                reelCommentRepository.save(comment)
        );
    }

    @Override
    public ReelCommentResponse updateComment(
            String userId,
            String commentId,
            ReelCommentRequest request) {

        ReelComment comment =
                reelCommentRepository
                        .findByCommentId(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found: "
                                                + commentId
                                ));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException(
                    "You can update only your comment"
            );
        }

        comment.setComment(
                request.getComment()
        );

        return mapToResponse(
                reelCommentRepository.save(comment)
        );
    }

    @Override
    public void deleteComment(
            String userId,
            String commentId) {

        ReelComment comment =
                reelCommentRepository
                        .findByCommentId(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found: "
                                                + commentId
                                ));

        if (!comment.getUserId().equals(userId)) {
            throw new RuntimeException(
                    "You can delete only your comment"
            );
        }

        comment.setStatus(
                CommentStatus.DELETED
        );

        reelCommentRepository.save(comment);
    }

    @Override
    public PageResponse<ReelCommentResponse> getComments(
            String reelId,
            int page,
            int size) {

        Page<ReelComment> commentPage =
                reelCommentRepository.findByReelIdAndStatus(
                        reelId,
                        CommentStatus.ACTIVE,
                        PageRequest.of(page, size)
                );

        List<ReelCommentResponse> content =
                commentPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                content,
                commentPage.getNumber(),
                commentPage.getSize(),
                commentPage.getTotalElements(),
                commentPage.getTotalPages(),
                commentPage.isFirst(),
                commentPage.isLast()
        );
    }

    private ReelCommentResponse mapToResponse(
            ReelComment comment) {

        ReelCommentResponse response =
                new ReelCommentResponse();

        response.setCommentId(comment.getCommentId());
        response.setUserId(comment.getUserId());
        response.setReelId(comment.getReelId());
        response.setComment(comment.getComment());
        response.setStatus(comment.getStatus());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        return response;
    }
}