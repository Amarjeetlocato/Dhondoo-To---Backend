package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.entity.Reel;

public interface ReelService {

    Reel createReel(
            Reel reel);

    Reel updateReel(
            String reelId,
            Reel reelDetails);

    Optional<Reel> getReelById(
            String reelId);

    List<Reel> getReelsByBusinessId(
            String businessId);

    List<Reel> searchReels(
            String description);

    List<Reel> getAllReels();

    void deleteReel(
            String reelId);

    ReelResponseDTO toResponseDTO(
            Reel reel);

    ReelSummaryDTO toSummaryDTO(
            Reel reel);

    PageResponse<ReelResponseDTO> getAllreels(
            Pageable pageable);
}