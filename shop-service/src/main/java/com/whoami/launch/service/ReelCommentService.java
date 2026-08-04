package com.whoami.launch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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

@Service
public class ReelCommentService {

    @Autowired
    private ReelCommentRepository reelCommentRepository;

    @Autowired
    private ReelRepository reelRepository;

    
    public ReelCommentResponse addComment(
            String userId,
            ReelCommentRequest request) {

        reelRepository.findById(
                request.getReelId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Reel not found"));

        ReelComment comment =
                new ReelComment();

        comment.setUserId(userId);
        comment.setReelId(
                request.getReelId());
        comment.setComment(
                request.getComment());

        comment.setStatus(
                CommentStatus.ACTIVE);

        return mapToResponse(
                reelCommentRepository.save(comment));
    }

    
    public ReelCommentResponse updateComment(
            String userId,
            Long commentId,
            ReelCommentRequest request) {

        ReelComment comment =
                reelCommentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        if (!comment.getUserId()
                .equals(userId)) {

            throw new RuntimeException(
                    "You can update only your comment");
        }

        comment.setComment(
                request.getComment());

        return mapToResponse(
                reelCommentRepository.save(comment));
    }

    
    public void deleteComment(
            String userId,
            Long commentId) {

        ReelComment comment =
                reelCommentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Comment not found"));

        if (!comment.getUserId()
                .equals(userId)) {

            throw new RuntimeException(
                    "You can delete only your comment");
        }

        comment.setStatus(
                CommentStatus.DELETED);

        reelCommentRepository.save(comment);
    }

    
    public PageResponse<ReelCommentResponse> getComments(
            String reelId,
            int page,
            int size) {

        Page<ReelComment> commentPage =
                reelCommentRepository.findByReelIdAndStatus(
                        reelId,
                        CommentStatus.ACTIVE,
                        PageRequest.of(page, size));

        List<ReelCommentResponse> content =
                commentPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .collect(Collectors.toList());

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

        response.setId(comment.getId());
        response.setUserId(comment.getUserId());
        response.setReelId(comment.getReelId());
        response.setComment(comment.getComment());
        response.setStatus(comment.getStatus());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());

        return response;
    }
}