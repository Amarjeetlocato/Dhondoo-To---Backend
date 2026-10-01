package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.NearbyBusinessDTO;
import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.dto.NearbyServiceDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.Location;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.LocationService;
import com.whoami.launch.service.NearbyService;
import com.whoami.launch.util.DistanceCalculator;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NearbyServiceImpl implements NearbyService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final ReelRepository reelRepository;
    private final LocationService locationService;

    @Override
    public List<NearbyBusinessDTO> findNearbyBusiness(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        double userLat = location.getLatitude();
        double userLon = location.getLongitude();

        return businessRepository.findAllWithCoordinates()
                .stream()
                .map(business -> {

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    userLat,
                                    userLon,
                                    business.getLatitude(),
                                    business.getLongitude()
                            );

                    return new NearbyBusinessDTO(
                            business.getBusinessId(),
                            business.getBusinessName(),
                            business.getUserId(),
                            business.getAddress(),
                            business.getLatitude(),
                            business.getLongitude(),
                            distance,
                            business.getMobileNumber()
                    );
                })
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()
                        ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyProductDTO> findNearbyProducts(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        double userLat = location.getLatitude();
        double userLon = location.getLongitude();

        return productRepository.findAllWithBusinessCoordinates()
                .stream()
                .map(product -> {

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            product.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    userLat,
                                    userLon,
                                    business.getLatitude(),
                                    business.getLongitude()
                            );

                    return new NearbyProductDTO(
                            product.getProductId(),
                            product.getProductName(),
                            product.getBusinessId(),
                            product.getProductPrice(),
                            product.getProductDescription(),
                            distance
                    );
                })
                .filter(java.util.Objects::nonNull)
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()
                        ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyServiceDTO> findNearbyServices(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        double userLat = location.getLatitude();
        double userLon = location.getLongitude();

        return serviceRepository
                .findAllFromBusinessesWithCoordinates()
                .stream()
                .map(service -> {

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            service.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    userLat,
                                    userLon,
                                    business.getLatitude(),
                                    business.getLongitude()
                            );

                    return new NearbyServiceDTO(
                            service.getServiceId(),
                            service.getServiceName(),
                            service.getBusinessId(),
                            business.getBusinessName(),
                            service.getPrice(),
                            service.getServiceDescription(),
                            distance
                    );
                })
                .filter(
                        (NearbyServiceDTO dto) ->
                                dto != null
                )
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()
                        ))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public List<NearbyReelDTO> findNearbyReels(
            String userId,
            Double radiusKm,
            int limit) {

        Optional<Location> userLocation =
                locationService.getLocationsByUserId(userId)
                        .stream()
                        .findFirst();

        if (userLocation.isEmpty()
                || userLocation.get().getLatitude() == null
                || userLocation.get().getLongitude() == null) {

            return List.of();
        }

        Location location = userLocation.get();

        double userLat = location.getLatitude();
        double userLon = location.getLongitude();

        return reelRepository.findAllFromBusinessesWithCoordinates()
                .stream()
                .map(reel -> {

                    if (reel.getBusinessId() == null) {
                        return null;
                    }

                    Business business =
                            businessRepository
                                    .findByBusinessId(
                                            reel.getBusinessId())
                                    .orElse(null);

                    if (business == null
                            || business.getLatitude() == null
                            || business.getLongitude() == null) {

                        return null;
                    }

                    double distance =
                            DistanceCalculator.calculateDistance(
                                    userLat,
                                    userLon,
                                    business.getLatitude(),
                                    business.getLongitude()
                            );

                    return new NearbyReelDTO(
                            reel.getReelId(),
                            reel.getBusinessId(),
                            reel.getReelVideo(),
                            reel.getReelDescription(),
                            distance
                    );
                })
                .filter(java.util.Objects::nonNull)
                .filter(dto -> dto.getDistance() <= radiusKm)
                .sorted((a, b) ->
                        Double.compare(
                                a.getDistance(),
                                b.getDistance()
                        ))
                .limit(limit)
                .collect(Collectors.toList());
    }
}