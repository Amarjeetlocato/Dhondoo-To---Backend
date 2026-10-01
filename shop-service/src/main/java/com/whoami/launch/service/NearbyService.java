package com.whoami.launch.service;

import java.util.List;

import com.whoami.launch.dto.NearbyBusinessDTO;
import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.dto.NearbyServiceDTO;

public interface NearbyService {

    List<NearbyBusinessDTO> findNearbyBusiness(
            String userId,
            Double radiusKm,
            int limit);

    List<NearbyProductDTO> findNearbyProducts(
            String userId,
            Double radiusKm,
            int limit);

    List<NearbyServiceDTO> findNearbyServices(
            String userId,
            Double radiusKm,
            int limit);

    List<NearbyReelDTO> findNearbyReels(
            String userId,
            Double radiusKm,
            int limit);
}