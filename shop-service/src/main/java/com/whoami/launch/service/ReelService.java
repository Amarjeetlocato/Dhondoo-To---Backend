package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.dto.ReelCreatedEvent;
import com.locato.dto.ReelDeletedEvent;
import com.locato.dto.ReelUpdatedEvent;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.entity.Reel;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.producer.ShopKafkaProducer;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ShopFollowerRepository;
import com.whoami.launch.repository.ShopRepository;
import com.locato.topics.KafkaTopics;

@Service
public class ReelService {

    @Autowired
    private ReelRepository reelRepository;

    @Autowired
    private ShopRepository shopRepository;

   
    @Autowired
    private ShopKafkaProducer shopKafkaProducer;
    
    @Autowired
    private ShopFollowerRepository followerRepository;
    
    private com.whoami.launch.util.notify notify;

    public Reel createReel(Reel reel) {

        Shop shop = shopRepository.findById(reel.getShopId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found with id: "
                                        + reel.getShopId()
                        ));

        Reel savedReel = reelRepository.save(reel);

        ReelCreatedEvent event =
                ReelCreatedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("REEL_CREATED")
                        .eventTime(LocalDateTime.now())

                        .reelId(savedReel.getReelId())
                        .shopId(shop.getShopId())
                        .shopName(shop.getShopName())
                        .userId(shop.getUserId())

                        .description(savedReel.getReelDescription())
                        .reelVideo(savedReel.getReelVideo())
                        .reelThumbnail(savedReel.getReelThumbnail())

                        .build();

        shopKafkaProducer.publish(
                KafkaTopics.REEL_EVENTS,
                event
        );
        return savedReel;
    }

    public Reel updateReel(
            String reelId,
            Reel reelDetails
    ) {

        Optional<Reel> reelOpt =
                reelRepository.findById(reelId);

        if (reelOpt.isEmpty()) {
            return null;
        }

        Reel existingReel = reelOpt.get();

        Shop shop = shopRepository.findById(
                existingReel.getShopId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Shop not found for reel: "
                                + existingReel.getReelId()
                ));
        StringBuilder changes =
                new StringBuilder();

        if (reelDetails.getReelVideo() != null) {
            existingReel.setReelVideo(
                    reelDetails.getReelVideo()
            );
            changes.append("Video updated. ");
        }

        if (reelDetails.getReelThumbnail() != null) {
            existingReel.setReelThumbnail(
                    reelDetails.getReelThumbnail()
            );
            changes.append("Thumbnail updated. ");
        }

        if (reelDetails.getReelDescription() != null) {
            existingReel.setReelDescription(
                    reelDetails.getReelDescription()
            );
            changes.append("Description updated. ");
        }

        if (reelDetails.getReelReviews() != null) {
            existingReel.setReelReviews(
                    reelDetails.getReelReviews()
            );
            changes.append("Reviews updated. ");
        }

        if (reelDetails.getReelRatings() != null) {
            existingReel.setReelRatings(
                    reelDetails.getReelRatings()
            );
            changes.append("Ratings updated. ");
        }

        Reel updatedReel =
                reelRepository.save(existingReel);
       
        ReelUpdatedEvent event =
                ReelUpdatedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("REEL_UPDATED")
                        .eventTime(LocalDateTime.now())

                        .reelId(updatedReel.getReelId())
                        .shopId(shop.getShopId())
                        .shopName(shop.getShopName())
                        .userId(shop.getUserId())

                        .description(updatedReel.getReelDescription())
                        .reelVideo(updatedReel.getReelVideo())
                        .reelThumbnail(updatedReel.getReelThumbnail())

                        .changes(changes.toString())
                        .build();
        	shopKafkaProducer.publish(
        	        KafkaTopics.REEL_EVENTS,
        	        event
        	);
        

        return updatedReel;

   
    }

    public Optional<Reel> getReelById(
            String reelId
    ) {
        return reelRepository.findById(reelId);
    }

    public List<Reel> getReelsByShopId(
            String shopId
    ) {
        return reelRepository.findByShopId(shopId);
    }

    public List<Reel> searchReels(
            String description
    ) {
        return reelRepository.findByReelDescriptionContaining(
                description
        );
    }

    public List<Reel> getAllReels() {
        return reelRepository.findAll();
    }

    public void deleteReel(
            String reelId
    ) {

        Optional<Reel> reelOpt =
                reelRepository.findById(reelId);

        if (reelOpt.isPresent()) {

            Reel reel = reelOpt.get();

            Shop shop = shopRepository.findById(
                    reel.getShopId()
            ).orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Shop not found for reel: "
                                    + reel.getReelId()
                    ));
            reelRepository.deleteById(reelId);

           
            ReelDeletedEvent event =
                    ReelDeletedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("REEL_DELETED")
                            .eventTime(LocalDateTime.now())

                            .reelId(reel.getReelId())
                            .shopId(shop.getShopId())
                            .shopName(shop.getShopName())
                            .userId(shop.getUserId())

                            .build();
                shopKafkaProducer.publish(
                        KafkaTopics.REEL_EVENTS,
                        event
                );
            
        }
    }

    public ReelResponseDTO toResponseDTO(
            Reel reel
    ) {

        if (reel == null) {
            return null;
        }

        return new ReelResponseDTO(
                reel.getReelId(),
                reel.getShopId(),
                reel.getReelVideo(),
                reel.getReelThumbnail(),
                reel.getReelDescription(),
                reel.getReelReviews(),
                reel.getReelRatings()
        );
    }

    public ReelSummaryDTO toSummaryDTO(
            Reel reel
    ) {

        if (reel == null) {
            return null;
        }

        return new ReelSummaryDTO(
                reel.getReelId(),
                reel.getShopId(),
                reel.getReelVideo(),
                reel.getReelDescription()
        );
    }

    
    public PageResponse<ReelResponseDTO> getAllreels(
            Pageable pageable) {

        Page<Reel> page = reelRepository.findAll(pageable);

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