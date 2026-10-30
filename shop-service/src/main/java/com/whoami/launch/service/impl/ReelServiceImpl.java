
package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.reel.ReelCreatedEvent;
import com.locato.constants.events.reel.ReelDeletedEvent;
import com.locato.constants.events.reel.ReelUpdatedEvent;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.entity.Reel;
import com.whoami.launch.enums.BusinessCapability;
import com.whoami.launch.producer.ReelEventProducer;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.service.BusinessAccessService;
import com.whoami.launch.service.ReelService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService {

    private final ReelRepository reelRepository;
    private final BusinessRepository businessRepository;
    private final BusinessAccessService businessAccessService;
    private final BusinessAccessStateRepository accessStateRepository;
    private final ReelEventProducer reelEventProducer;

    @Override
    @Transactional
    public Reel createReel(Reel reel) {
        if (reel == null) {
            reel = new Reel();
        }

        if (reel.getBusinessId() != null && !reel.getBusinessId().isBlank()) {
            long currentReelCount = reelRepository.countByBusinessId(reel.getBusinessId());
            businessAccessService.validateCreationAccess(
                    reel.getBusinessId(),
                    BusinessCapability.REEL,
                    currentReelCount
            );
        }

        Reel savedReel = reelRepository.save(reel);

        try {
            if (savedReel.getBusinessId() != null) {
                businessRepository.findByBusinessId(savedReel.getBusinessId())
                        .ifPresent(business ->
                                reelEventProducer.publishReelCreated(
                                        ReelCreatedEvent.builder()
                                                .reelId(savedReel.getReelId())
                                                .reelThumbnail(savedReel.getReelThumbnail())
                                                .reelVideo(savedReel.getReelVideo())
                                                .description(savedReel.getReelDescription())
                                                .businessId(business.getBusinessId())
                                                .businessName(business.getBusinessName())
                                                .businessLogo(null)
                                                .businessBanner(null)
                                                .userId(business.getUserId())
                                                .build()
                                )
                        );
            }
        } catch (Exception ignored) {
        }

        return savedReel;
    }

    @Override
    @Transactional
    public Reel updateReel(String reelId, Reel reelDetails) {

        Reel existingReel = reelRepository.findByReelId(reelId).orElse(null);

        if (existingReel == null) {
            return null;
        }

        BusinessAccessState accessState = accessStateRepository
                .findByBusinessId(existingReel.getBusinessId())
                .orElseGet(() -> {
                    BusinessAccessState state = new BusinessAccessState();
                    state.setBusinessId(existingReel.getBusinessId());
                    return state;
                });

        if (!accessState.isSubscriptionActive()) {
            throw new IllegalStateException(
                    "Reels cannot be updated without an active subscription."
            );
        }

        StringBuilder changes = new StringBuilder();

        if (reelDetails.getReelVideo() != null) {
            existingReel.setReelVideo(reelDetails.getReelVideo());
            changes.append("reelVideo,");
        }

        if (reelDetails.getReelThumbnail() != null) {
            existingReel.setReelThumbnail(reelDetails.getReelThumbnail());
            changes.append("reelThumbnail,");
        }

        if (reelDetails.getReelDescription() != null) {
            existingReel.setReelDescription(reelDetails.getReelDescription());
            changes.append("reelDescription,");
        }

        if (reelDetails.getReelReviews() != null) {
            existingReel.setReelReviews(reelDetails.getReelReviews());
            changes.append("reelReviews,");
        }

        if (reelDetails.getReelRatings() != null) {
            existingReel.setReelRatings(reelDetails.getReelRatings());
            changes.append("reelRatings,");
        }

        Reel savedReel = reelRepository.save(existingReel);

        if (!changes.isEmpty()) {
            businessRepository.findByBusinessId(savedReel.getBusinessId())
                    .ifPresent(business ->
                            reelEventProducer.publishReelUpdated(
                                    ReelUpdatedEvent.builder()
                                            .reelId(savedReel.getReelId())
                                            .reelThumbnail(savedReel.getReelThumbnail())
                                            .reelVideo(savedReel.getReelVideo())
                                            .description(savedReel.getReelDescription())
                                            .businessId(business.getBusinessId())
                                            .businessName(business.getBusinessName())
                                            .businessLogo(null)
                                            .businessBanner(null)
                                            .userId(business.getUserId())
                                            .changes(changes.toString().replaceAll(",$", ""))
                                            .build()
                            )
                    );
        }

        return savedReel;
    }

    @Override
    public Optional<Reel> getReelById(String reelId) {
        return reelRepository.findByReelId(reelId);
    }

    @Override
    public List<Reel> getReelsByBusinessId(String businessId) {
        return reelRepository.findByBusinessId(businessId);
    }

    @Override
    public List<Reel> searchReels(String description) {
        return reelRepository.findByReelDescriptionContaining(description);
    }

    @Override
    public List<Reel> getAllReels() {
        return reelRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteReel(String reelId) {

        Reel reel = reelRepository.findByReelId(reelId).orElse(null);

        if (reel == null) {
            return;
        }

        businessRepository.findByBusinessId(reel.getBusinessId())
                .ifPresent(business ->
                        reelEventProducer.publishReelDeleted(
                                ReelDeletedEvent.builder()
                                        .reelId(reel.getReelId())
                                        .reelThumbnail(reel.getReelThumbnail())
                                        .businessId(business.getBusinessId())
                                        .businessName(business.getBusinessName())
                                        .businessLogo(null)
                                        .businessBanner(null)
                                        .userId(business.getUserId())
                                        .build()
                        )
                );

        reelRepository.delete(reel);
    }

    @Override
    public ReelResponseDTO toResponseDTO(Reel reel) {
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
    public ReelSummaryDTO toSummaryDTO(Reel reel) {
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
    public PageResponse<ReelResponseDTO> getAllreels(Pageable pageable) {

        Page<Reel> page = reelRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(this::toResponseDTO).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast()
        );
    }
}