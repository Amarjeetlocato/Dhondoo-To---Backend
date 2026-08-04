package com.whoami.launch.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Entity
@Table(name = "reels")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Reel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String reelId;
    
    @Column(nullable = false)
    private String shopId;
    
    
    
    @Column
    private String reelVideo;
    
    @Column
    private String reelThumbnail;

    @Column
    private String reelthumbnailPublicId;

    @Column
    private String reelvideoPublicId;
    
    @Column(columnDefinition = "TEXT")
    private String reelDescription;
    
    @Column(columnDefinition = "TEXT")
    private String reelReviews;
    
    @Column
    private Double reelRatings;
    
   
}
