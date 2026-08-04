package com.whoami.launch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.ReelComment;
import com.whoami.launch.enums.CommentStatus;

@Repository
public interface ReelCommentRepository extends JpaRepository<ReelComment, Long> {

    Page<ReelComment> findByReelIdAndStatus(
            String reelId,
            CommentStatus status,
            Pageable pageable);

    long countByReelIdAndStatus(
            String reelId,
            CommentStatus status);
}