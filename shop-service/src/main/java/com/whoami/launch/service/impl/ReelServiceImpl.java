package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.entity.Reel;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.service.ReelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReelServiceImpl
        implements ReelService {

    private final ReelRepository reelRepository;

    @Override
    public Reel createReel(
            Reel reel) {

        return reelRepository.save(reel);
    }

    @Override
    public Reel updateReel(
            String reelId,
            Reel reelDetails) {

        Reel existingReel =
                reelRepository
                        .findByReelId(reelId)
                        .orElse(null);

        if (existingReel == null) {
            return null;
        }

        if (reelDetails.getReelVideo() != null) {
            existingReel.setReelVideo(
                    reelDetails.getReelVideo()
            );
        }

        if (reelDetails.getReelThumbnail() != null) {
            existingReel.setReelThumbnail(
                    reelDetails.getReelThumbnail()
            );
        }

        if (reelDetails.getReelDescription() != null) {
            existingReel.setReelDescription(
                    reelDetails.getReelDescription()
            );
        }

        if (reelDetails.getReelReviews() != null) {
            existingReel.setReelReviews(
                    reelDetails.getReelReviews()
            );
        }

        if (reelDetails.getReelRatings() != null) {
            existingReel.setReelRatings(
                    reelDetails.getReelRatings()
            );
        }

        return reelRepository.save(existingReel);
    }

    @Override
    public Optional<Reel> getReelById(
            String reelId) {

        return reelRepository.findByReelId(
                reelId
        );
    }

    @Override
    public List<Reel> getReelsByBusinessId(
            String businessId) {

        return reelRepository.findByBusinessId(
                businessId
        );
    }

    @Override
    public List<Reel> searchReels(
            String description) {

        return reelRepository
                .findByReelDescriptionContaining(
                        description
                );
    }

    @Override
    public List<Reel> getAllReels() {

        return reelRepository.findAll();
    }

    @Override
    public void deleteReel(
            String reelId) {

        Reel reel =
                reelRepository
                        .findByReelId(reelId)
                        .orElse(null);

        if (reel != null) {
            reelRepository.delete(reel);
        }
    }

    @Override
    public ReelResponseDTO toResponseDTO(
            Reel reel) {

        if (reel == null) {
            return null;
        }

        return new ReelResponseDTO(
                reel.getReelId(),
                reel.getBusinessId(),
                reel.getReelVideo(),
                reel.getReelThumbnail(),
                reel.getReelDescription(),
                reel.getReelReviews(),
                reel.getReelRatings()
        );
    }

    @Override
    public ReelSummaryDTO toSummaryDTO(
            Reel reel) {

        if (reel == null) {
            return null;
        }

        return new ReelSummaryDTO(
                reel.getReelId(),
                reel.getBusinessId(),
                reel.getReelVideo(),
                reel.getReelDescription()
        );
    }

    @Override
    public PageResponse<ReelResponseDTO> getAllreels(
            Pageable pageable) {

        Page<Reel> page =
                reelRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::toResponseDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}